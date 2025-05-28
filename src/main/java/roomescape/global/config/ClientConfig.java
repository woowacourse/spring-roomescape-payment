package roomescape.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.reservation.service.PaymentRestClient;
import roomescape.reservation.error.handler.PaymentResponseErrorHandler;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final PaymentResponseErrorHandler paymentResponseErrorHandler;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }
//    @Bean
//    public PaymentRestClient paymentRestClient() {
//        return new PaymentRestClient(
//                RestClient.builder()
//                        .baseUrl("https://api.tosspayments.com/v1/payments")
//                        .build(),
//                paymentResponseErrorHandler
//        );
//    }
}
