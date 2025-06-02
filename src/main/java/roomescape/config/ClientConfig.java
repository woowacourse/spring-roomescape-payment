package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.infraStructure.PaymentClientSelector;
import roomescape.payment.infraStructure.toss.TossPaymentClient;

import java.time.Duration;

@Configuration
public class ClientConfig {

    @Value("${payment.toss.base-url}")
    private String tossPaymentBaseUrl;

    @Bean
    public TossPaymentClient getReservationPaymentClient() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setReadTimeout(Duration.ofSeconds(30));
        simpleClientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(30));

        return new TossPaymentClient(
                RestClient.builder()
                        .requestFactory(simpleClientHttpRequestFactory)
                        .baseUrl(tossPaymentBaseUrl).build()
        );
    }

    @Bean
    public PaymentClientSelector paymentClientSelector(TossPaymentClient tossPaymentClient) {
        return new PaymentClientSelector(tossPaymentClient);
    }
}
