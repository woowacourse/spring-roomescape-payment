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
import roomescape.dto.payment.PaymentConfirmResponse;

@Component
public class TossPaymentClient {

    private final RestClient.Builder restClient;
    private final ResponseErrorHandler errorHandler;

    @Value("${payment.toss.base-uri}")
    private String tossPaymentsBaseUri;

    @Value("${payment.toss.confirm-uri}")
    private String confirmUri;

    @Value("${payment.toss.secret}")
    private String secret;

    public TossPaymentClient(RestClient.Builder restClient, ResponseErrorHandler errorHandler) {
        this.restClient = restClient.baseUrl(tossPaymentsBaseUri)
                .defaultHeaders(this::setHeaders);
        this.errorHandler = errorHandler;
    }

    private void setHeaders(HttpHeaders headers) {
        headers.setBasicAuth(authorization());
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private String authorization() {
        String secretKeyWithoutPassword = secret + ":";
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(secretKeyWithoutPassword.getBytes(StandardCharsets.UTF_8));
    }

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest paymentConfirmRequest) {
        return restClient.build()
                .post()
                .uri(confirmUri)
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(errorHandler)
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
