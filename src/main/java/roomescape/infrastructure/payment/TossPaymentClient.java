package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

import java.util.Base64;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentClient implements PaymentClient {

    private static final String ENCODING_FORMAT = "%s:";
    private static final String SECRET_KEY_PREFIX = "Basic ";

    private final RestClient restClient;
    private final TossPaymentProperties properties;

    public TossPaymentClient(RestClient.Builder restClientBuilder, TossPaymentClientErrorHandler errorHandler, TossPaymentProperties properties) {
        this.restClient = restClientBuilder
                .defaultStatusHandler(errorHandler)
                .defaultHeader(HttpHeaders.AUTHORIZATION, SECRET_KEY_PREFIX + getEncodedSecretKey(properties.getSecretKey()))
                .build();
        this.properties = properties;
    }

    private String getEncodedSecretKey(String secretKey) {
        return Base64.getEncoder().encodeToString(String.format(ENCODING_FORMAT, secretKey).getBytes());
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        restClient.post()
                .uri(properties.getPaymentUri())
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
