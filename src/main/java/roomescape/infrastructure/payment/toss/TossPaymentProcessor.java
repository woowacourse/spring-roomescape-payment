package roomescape.infrastructure.payment.toss;

import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.dto.PaymentConfirmRequest;
import roomescape.domain.payment.dto.PaymentConfirmResponse;
import roomescape.domain.payment.dto.TossPaymentConfirmRequest;
import roomescape.domain.payment.dto.TossPaymentConfirmResponse;
import roomescape.domain.payment.service.PaymentProcessor;

public class TossPaymentProcessor implements PaymentProcessor {

    private final String secretKey;
    private final RestClient restClient;
    private final String confirmUrl;

    public TossPaymentProcessor(
            final String secretKey,
            final RestClient restClient,
            final String confirmUrl
    ) {
        this.secretKey = secretKey;
        this.restClient = restClient;
        this.confirmUrl = confirmUrl;
    }

    public PaymentConfirmResponse processPayment(final PaymentConfirmRequest request) {
        final TossPaymentConfirmRequest tossRequest = (TossPaymentConfirmRequest) request;

        return restClient.post()
                .uri(confirmUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", createBasicAuthHeader(secretKey))
                .body(tossRequest)
                .retrieve()
                .body(TossPaymentConfirmResponse.class);
    }

    private String createBasicAuthHeader(final String secretKey) {
        final String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":base64").getBytes());

        return "Basic " + encodedSecretKey;
    }

    @Override
    public boolean supports(final PaymentType paymentType) {
        return paymentType == PaymentType.TOSS;
    }
}
