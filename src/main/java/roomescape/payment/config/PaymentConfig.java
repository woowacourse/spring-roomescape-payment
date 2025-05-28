package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.interceptor.PaymentResponseInterceptor;
import roomescape.payment.service.PaymentClient;

@Configuration
public class PaymentConfig {

    private static final String COLON = ":";

    private final ObjectMapper objectMapper;
    private final String token;

    public PaymentConfig(ObjectMapper objectMapper, @Value("${payment.token}") String token) {
        this.objectMapper = objectMapper;
        this.token = token;
    }

    @Bean
    public PaymentClient paymentClient() {
        RestClient client = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .requestInterceptor(new PaymentResponseInterceptor(objectMapper))
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString(((token + COLON).getBytes())))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(client))
                .build();

        return factory.createClient(PaymentClient.class);
    }
}
