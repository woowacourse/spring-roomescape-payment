package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentConfig {

    @Value("${payment.api.baseurl}")
    private String baseUrl;

    @Bean
    public RestClient paymentRestClient() {
        return RestClient.builder()
                        .baseUrl(baseUrl)
                        .build();
    }
}
