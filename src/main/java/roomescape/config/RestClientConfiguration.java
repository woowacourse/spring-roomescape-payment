package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.service.PaymentClientService;

@Configuration
public class RestClientConfiguration {

    @Bean
    public PaymentClientService createPaymentService() {
//        var clientFactory = new HttpComponentsClientHttpRequestFactory();
//        //todo: connect 와 read Timeout의 차이 학습하기
//        clientFactory.setConnectTimeout(5000);
//        clientFactory.setReadTimeout(5000);

        RestClient restClient = RestClient.builder()
//                .requestFactory(clientFactory)
                .baseUrl("https://api.tosspayments.com/v1")
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(PaymentClientService.class);
    }
}
