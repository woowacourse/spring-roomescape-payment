package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.service.PaymentClientService;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestClient.Builder restClientBuilder() {
        var clientFactory = new HttpComponentsClientHttpRequestFactory();
        clientFactory.setConnectTimeout(5000);
        clientFactory.setReadTimeout(5000);

        return RestClient.builder()
                .requestFactory(clientFactory)
                .baseUrl("https://api.tosspayments.com/v1");
    }

    @Bean
    public RestClient restClient() {
        Builder builder = restClientBuilder();
        return builder.build();
    }

    @Bean
    public PaymentClientService createPaymentService() {
        RestClient restClient = restClient();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(PaymentClientService.class);
    }
}
