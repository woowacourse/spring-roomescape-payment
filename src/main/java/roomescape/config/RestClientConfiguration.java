package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.service.PaymentService;

@Configuration
public class RestClientConfiguration {

    @Bean
    public PaymentService createPaymentService() {
        RestClient restClient = RestClient.builder().baseUrl("https://api.tosspayments.com/v1").build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(PaymentService.class);
    }
}
