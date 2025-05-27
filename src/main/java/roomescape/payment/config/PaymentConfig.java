package roomescape.payment.config;

import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.service.PaymentClient;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        RestClient client = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:".getBytes())))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(client))
                .build();

        return factory.createClient(PaymentClient.class);
    }
}
