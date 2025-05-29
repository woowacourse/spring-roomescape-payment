package roomescape.payment.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.PaymentClient;

@TestConfiguration
public class TestPaymentConfiguration {

    @Value("${payment.api.base-url}")
    private String URL;

    @Bean
    @Primary
    public PaymentClient paymentClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder
                .baseUrl(URL)
                .defaultHeader("Content-Type", "application/json")
                .build();

        return new PaymentClient(restClient);
    }
}
