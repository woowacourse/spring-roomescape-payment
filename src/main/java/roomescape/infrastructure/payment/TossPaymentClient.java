package roomescape.infrastructure.payment;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

import java.util.Base64;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String ENCODING_FORMAT = "%s:";
    private static final String SECRET_KEY_PREFIX = "Basic ";

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;
    private final TossPaymentProperties properties;
    private final String encodedSecretKey;

    public TossPaymentClient(RestClient restClient,
                             TossPaymentClientErrorHandler tossPaymentClientErrorHandler,
                             TossPaymentProperties properties) {
        this.restClient = restClient;
        this.tossPaymentClientErrorHandler = tossPaymentClientErrorHandler;
        this.properties = properties;
        this.encodedSecretKey = Base64.getEncoder()
                .encodeToString(String.format(ENCODING_FORMAT, properties.getSecretKey()).getBytes());
    }

    @Override
    public void confirm(PaymentRequest paymentRequest) {
        restClient.post()
                .uri(properties.getPaymentUri())
                .header(HttpHeaders.AUTHORIZATION, SECRET_KEY_PREFIX + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(tossPaymentClientErrorHandler)
                .toBodilessEntity();
    }
}
