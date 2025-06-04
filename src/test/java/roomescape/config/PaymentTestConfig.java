package roomescape.config;

import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.reservation.payment.domain.PaymentMethod;
import roomescape.reservation.payment.dto.request.PaymentRequest;
import roomescape.reservation.payment.gateway.PaymentGateway;
import roomescape.reservation.payment.gateway.PaymentGatewayResolver;

@TestConfiguration
public class PaymentTestConfig {

    @Bean
    public PaymentGateway tossGateway() {
        return new PaymentGateway() {
            @Override
            public PaymentMethod supports() {
                return PaymentMethod.TOSS;
            }

            @Override
            public void confirm(PaymentRequest request) {
            }
        };
    }

    @Bean
    public PaymentGatewayResolver resolver(List<PaymentGateway> gateways) {
        return new PaymentGatewayResolver(gateways);
    }
}
