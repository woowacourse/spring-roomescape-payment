package roomescape.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import roomescape.application.exception.PaymentException;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.infrastructure.thirdparty.TossPaymentRestClient;
import roomescape.presentation.dto.request.PaymentProcessRequest;

@Service
public class PaymentService {

    private final TossPaymentRestClient tossPaymentRestClient;
    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;

    public PaymentService(TossPaymentRestClient tossPaymentRestClient,
                          ObjectMapper objectMapper,
                          PaymentRepository paymentRepository
    ) {
        this.tossPaymentRestClient = tossPaymentRestClient;
        this.objectMapper = objectMapper;
        this.paymentRepository = paymentRepository;
    }

    public Payment process(PaymentProcessRequest request) {
        ResponseEntity<String> paymentResponse = tossPaymentRestClient.getPaymentResponse(request);
        JsonNode jsonNode = getJsonNode(paymentResponse.getBody());
        String paymentKey = jsonNode.get("paymentKey").asText();
        String orderId = jsonNode.get("orderId").asText();
        int totalAmount = jsonNode.get("totalAmount").asInt();
        Payment payment = Payment.create(paymentKey, orderId, totalAmount);
        return paymentRepository.save(payment);
    }

    private JsonNode getJsonNode(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new PaymentException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
