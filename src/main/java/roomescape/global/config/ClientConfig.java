package roomescape.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.reservation.client.TossPaymentsClient;

@Configuration
public class ClientConfig {

    private final String secretKey;

    public ClientConfig(@Value("${toss.payments.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    public TossPaymentsClient tossRestClient() {
        return new TossPaymentsClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments/")
                        .build()
                , secretKey
        );
    }
}
