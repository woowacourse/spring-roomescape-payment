package roomescape.payment.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.global.error.exception.ServerException;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.dto.response.PaymentErrorResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, RestClient restClient) {
        this.paymentRepository = paymentRepository;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.restClient = restClient;
    }

    public Long confirmPayment(String paymentKey, String orderId, Long amount) {
        PaymentConfirmResponse response = restClient.post()
                .body(new PaymentConfirmRequest(paymentKey, orderId, amount))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                            res.getBody(),
                            PaymentErrorResponse.class
                    );
                    throw new BadRequestException(paymentErrorResponse.message());
                })
                .onStatus(HttpStatusCode::is5xxServerError, ((req, res) -> {
                    PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                            res.getBody(),
                            PaymentErrorResponse.class
                    );
                    throw new ServerException(paymentErrorResponse.message());
                }))
                .body(PaymentConfirmResponse.class);

        Payment payment = new Payment(
                response.paymentKey(),
                response.orderId(),
                response.totalAmount(),
                response.type()
        );
        paymentRepository.save(payment);
        return payment.getId();
    }
}
