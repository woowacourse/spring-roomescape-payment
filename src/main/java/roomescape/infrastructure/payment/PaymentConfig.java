package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            @Value("${payment.toss.secret-key}") String secretKey,
            @Value("${payment.toss.timeout.read}") int readTimeOut,
            @Value("${payment.toss.timeout.connect}") int connectTimeOut,
            @Value("${payment.toss.base-url}") String baseUrl
    ) {
        RestClient tossRestClient = createTossRestClient(restClientBuilder, readTimeOut, connectTimeOut, baseUrl);
        return new TossPaymentClient(tossRestClient, objectMapper, secretKey);
    }

    private RestClient createTossRestClient(RestClient.Builder restClientBuilder, int readTimeOut, int connectTimeOut,
                                            String baseUrl) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeOut);
        factory.setReadTimeout(readTimeOut);
        return restClientBuilder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}
