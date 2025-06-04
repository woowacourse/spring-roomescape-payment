package roomescape.infrastructure.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public TossPaymentClient restClient(RestClient.Builder builder, ObjectMapper objectMapper,
                                        @Value("${payment.secret-key}") String secretKey) {
        return new TossPaymentClient(builder, objectMapper, secretKey);
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5_000);
            factory.setReadTimeout(5_000);

            restClientBuilder.requestFactory(factory)
                    .baseUrl("https://api.tosspayments.com");
        };
    }
}
