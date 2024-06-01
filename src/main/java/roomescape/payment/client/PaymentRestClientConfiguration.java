package roomescape.payment.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.toss.TossPaymentErrorHandler;

@Configuration
public class PaymentRestClientConfiguration {

    private static final String BASIC_PREFIX = "Basic ";

    private final PaymentProperties paymentProperties;

    public PaymentRestClientConfiguration(final PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public RestClient restClient() {
        byte[] encodedBytes = Base64.getEncoder()
                .encode((paymentProperties.getSecretKey() + paymentProperties.getPassword())
                        .getBytes(StandardCharsets.UTF_8));

        String authorizations = BASIC_PREFIX + new String(encodedBytes);

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorizations)
                .defaultStatusHandler(new TossPaymentErrorHandler())
                .build();
    }
}
