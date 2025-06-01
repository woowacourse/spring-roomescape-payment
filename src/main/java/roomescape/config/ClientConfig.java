package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.service.ReservationPaymentClient;

@Configuration
public class ClientConfig {

    @Value("${payment.toos.base-url}")
    private String tossPaymentBaseUrl;

    @Bean
    public ReservationPaymentClient getReservationPaymentClient() {
        return new ReservationPaymentClient(
                RestClient.builder().baseUrl(tossPaymentBaseUrl).build()
        );
    }
}
