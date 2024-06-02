package roomescape.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import roomescape.config.PaymentErrorHandler;
import roomescape.config.properties.PaymentClientProperties;

public class TossPaymentClientFactory implements PaymentClientFactory {
    private final PaymentClientProperties properties;

    public TossPaymentClientFactory(PaymentClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public RestClient createPaymentClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorization())
                .defaultStatusHandler(new PaymentErrorHandler())
                .build();
    }

    private String createAuthorization() {
        byte[] encodedBytes = Base64.getEncoder().encode((properties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
