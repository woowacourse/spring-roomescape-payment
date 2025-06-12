package roomescape.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.dto.response.PaymentErrorResponse;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.exception.custom.PaymentException;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            @Qualifier("tossClient") RestClient restClient,
            ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest paymentRequest, Reservation reservation) {
        String authorizations = getAuthorizationToken();

        ConfirmPaymentResponse body = restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                            response.getBody().readAllBytes(),
                            PaymentErrorResponse.class);
                    throw new PaymentException(response.getStatusCode(), paymentErrorResponse.message());
                }))
                .toEntity(ConfirmPaymentResponse.class)
                .getBody();

        Payment payment = new Payment(body.orderId(), body.totalAmount(), body.paymentKey(), body.type(), reservation);
        paymentRepository.save(payment);

        return body;
    }

    private String getAuthorizationToken() {
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
