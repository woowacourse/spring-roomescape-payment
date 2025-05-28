package roomescape.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.reservation.PaymentRestClient;
import roomescape.reservation.exception.PaymentResponseErrorHandler;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final PaymentResponseErrorHandler paymentResponseErrorHandler;

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments")
                        .build(),
                paymentResponseErrorHandler
        );
    }
}
