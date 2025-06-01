package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.infraStructure.PaymentClientSelector;
import roomescape.payment.infraStructure.toss.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Value("${payment.toss.base-url}")
    private String tossPaymentBaseUrl;

    @Bean
    public TossPaymentClient getReservationPaymentClient() {
        return new TossPaymentClient(
                RestClient.builder().baseUrl(tossPaymentBaseUrl).build()
        );
    }

    @Bean
    public PaymentClientSelector paymentClientSelector(TossPaymentClient tossPaymentClient) {
        return new PaymentClientSelector(tossPaymentClient);
    }
}
