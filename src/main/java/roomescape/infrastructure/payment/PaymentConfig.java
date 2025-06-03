package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.toss.TossPaymentClient;

@Configuration
public class PaymentConfig {

    @Bean
    public TossPaymentClient tossPaymentClient(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${payment.secret-key}") String secretKey
    ) {
        return new TossPaymentClient(createTossRestClient(restClientBuilder), objectMapper, secretKey);
    }

    private RestClient createTossRestClient(RestClient.Builder restClientBuilder) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(30));
        return restClientBuilder
                .baseUrl("https://api.tosspayments.com/")
                .requestFactory(factory)
                .build();
    }
}
