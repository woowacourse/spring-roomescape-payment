package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.client.payment.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public TossPaymentClient tossPaymentClient(
            @Value("${payment.toss.widget-secret-key}") String widgetSecretKey,
            @Value("${payment.toss.base-url}") String baseUrl,
            @Value("${payment.toss.confirm-url}") String confirmUrl) {
        RestClient restClient = RestClient.builder()
                .requestFactory(getClientHttpRequestFactory())
                .build();

        return new TossPaymentClient(widgetSecretKey, baseUrl, confirmUrl, restClient);
    }

    private JdkClientHttpRequestFactory getClientHttpRequestFactory() {
        JdkClientHttpRequestFactory clientHttpRequestFactory = new JdkClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(3000);

        return clientHttpRequestFactory;
    }
}
