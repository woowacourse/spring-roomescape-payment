package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.interceptor.PaymentResponseInterceptor;
import roomescape.payment.service.PaymentClient;

@TestConfiguration
public class TestPaymentConfig {

    private static final String COLON = ":";

    private final ObjectMapper objectMapper;
    private final String token;

    public TestPaymentConfig(ObjectMapper objectMapper, @Value("${payment.token}") String token) {
        this.objectMapper = objectMapper;
        this.token = token;
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .requestInterceptor(new PaymentResponseInterceptor(objectMapper))
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString(((token + COLON).getBytes())));
    }

    @Bean
    public PaymentClient paymentClient(RestClient.Builder builder) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(builder.build()))
                .build();

        return factory.createClient(PaymentClient.class);
    }

}
