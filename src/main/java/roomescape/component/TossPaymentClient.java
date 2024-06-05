package roomescape.component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import roomescape.dto.payment.PaymentConfirmRequest;

@Component
public class TossPaymentClient {

    private static final String TOSS_PAYMENTS_CONFIRM_URI = "/confirm";

    private final RestClient restClient;
    private final ResponseErrorHandler errorHandler;

    @Value("${toss.payments.secret}")
    private String secret;

    public TossPaymentClient(RestClient restClient, ResponseErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public void confirm(final PaymentConfirmRequest paymentConfirmRequest) {
        restClient.post()
                .uri(TOSS_PAYMENTS_CONFIRM_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, basicAuthorization())
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(errorHandler);
    }

    private String basicAuthorization() {
        final String secretKeyWithoutPassword = secret + ":";
        final Encoder encoder = Base64.getEncoder();
        String credentials =  encoder.encodeToString(secretKeyWithoutPassword.getBytes(StandardCharsets.UTF_8));
        return "Basic " + credentials;
    }
}
