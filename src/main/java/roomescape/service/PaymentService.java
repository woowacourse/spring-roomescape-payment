package roomescape.service;

import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.TossPaymentClientErrorHandler;
import roomescape.service.dto.request.PaymentRequest;

public class PaymentService {
    private static final String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    public PaymentService(RestClient restClient, TossPaymentClientErrorHandler tossPaymentClientErrorHandler) {
        this.restClient = restClient;
        this.tossPaymentClientErrorHandler = tossPaymentClientErrorHandler;
    }

    public void pay(PaymentRequest paymentRequest) {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + encoded)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(tossPaymentClientErrorHandler)
                .toBodilessEntity();
    }
}
