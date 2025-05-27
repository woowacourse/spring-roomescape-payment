package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.interceptor.PaymentResponseInterceptor;
import roomescape.payment.service.PaymentClient;

@Configuration
@RequiredArgsConstructor
public class PaymentConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public PaymentClient paymentClient() {
        RestClient client = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .requestInterceptor(new PaymentResponseInterceptor(objectMapper))
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString(("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:".getBytes())))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(client))
                .build();

        return factory.createClient(PaymentClient.class);
    }
}
