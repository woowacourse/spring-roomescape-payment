package roomescape.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import roomescape.config.PaymentErrorHandler;
import roomescape.config.properties.PaymentClientProperty;

public class PaymentClientFactory {
    private final PaymentClientProperty property;

    public PaymentClientFactory(PaymentClientProperty property) {
        this.property = property;
    }

    public RestClient createPaymentClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorization())
                .defaultStatusHandler(new PaymentErrorHandler())
                .build();
    }

    private String createAuthorization() {
        byte[] encodedBytes = Base64.getEncoder().encode((property.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
