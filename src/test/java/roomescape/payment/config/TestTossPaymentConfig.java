package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import roomescape.payment.interceptor.TossPaymentResponseInterceptor;

@TestConfiguration
public class TestTossPaymentConfig {

    private static final String COLON = ":";

    private final ObjectMapper objectMapper;
    private final String token;

    @Value("${payment.connection-timeout}")
    private int connectTimeoutMs;

    public TestTossPaymentConfig(ObjectMapper objectMapper, @Value("${payment.token}") String token) {
        this.objectMapper = objectMapper;
        this.token = token;
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .requestInterceptor(new TossPaymentResponseInterceptor(objectMapper))
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString(((token + COLON).getBytes())));
    }

    @Bean
    public TossPaymentClient paymentClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(restClientBuilder().build()))
                .build();

        return factory.createClient(TossPaymentClient.class);
    }
}
