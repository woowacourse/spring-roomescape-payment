package roomescape.payment.processor.toss;

import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.processor.PaymentConfirmRequest;
import roomescape.payment.processor.PaymentConfirmResponse;
import roomescape.payment.processor.PaymentProcessor;
import roomescape.payment.processor.PaymentType;

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

    @Override
    public boolean supports(final PaymentType paymentType) {
        return paymentType == PaymentType.TOSS;
    }

    private String createBasicAuthHeader(final String secretKey) {
        final String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":base64").getBytes());

        return "Basic " + encodedSecretKey;
    }
}
