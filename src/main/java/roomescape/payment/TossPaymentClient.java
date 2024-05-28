package roomescape.payment;

import java.util.Base64;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public class TossPaymentClient {

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";

    private final RestClient restClient;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirmPayments(PaymentConfirmRequest request) {

        restClient.post().uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(WIDGET_SECRET_KEY.getBytes()))
                .body(request)
                .retrieve()
                .toEntity(PaymentConfirmResponse.class);
    }
}
