package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.infrastructure.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentClient paymentClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10_000); // ms
        factory.setReadTimeout(10_000);

        return new TossPaymentClient(RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://api.tosspayments.com/v1").build());
    }
}
