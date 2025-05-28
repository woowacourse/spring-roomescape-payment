package roomescape.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import roomescape.application.dto.PaymentProcessRequest;
import roomescape.application.exception.PaymentException;
import roomescape.infrastructure.thirdparty.PaymentRestClient;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.presentation.dto.response.ErrorResponse;

@Service
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRestClient paymentRestClient,
                          ObjectMapper objectMapper,
                          PaymentRepository paymentRepository
    ) {
        this.paymentRestClient = paymentRestClient;
        this.objectMapper = objectMapper;
        this.paymentRepository = paymentRepository;
    }

    public Payment process(PaymentProcessRequest request) {
        ResponseEntity<String> paymentResponse = paymentRestClient.getPaymentResponse(request);
        String body = paymentResponse.getBody();

        JsonNode jsonNode = getJsonNode(body);

        if (paymentResponse.getStatusCode() == HttpStatus.OK) {
            String paymentKey = jsonNode.get("paymentKey").asText();
            String orderId = jsonNode.get("orderId").asText();
            Payment payment = Payment.create(paymentKey, orderId);
            return paymentRepository.save(payment);
        }

        String message = jsonNode.get("message").asText();

        if (paymentResponse.getStatusCode() == HttpStatusCode.valueOf(401) ||
            paymentResponse.getStatusCode().is5xxServerError()) {
            throw new PaymentException("알 수 없는 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        throw new PaymentException(message, HttpStatus.BAD_REQUEST);
    }

    private JsonNode getJsonNode(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new PaymentException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
