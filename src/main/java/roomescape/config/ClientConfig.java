package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.service.ReservationPaymentClient;

@Configuration
public class ClientConfig {
    @Bean
    public ReservationPaymentClient getReservationPaymentClient() {
        return new ReservationPaymentClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments/").build()
        );
    }
}
