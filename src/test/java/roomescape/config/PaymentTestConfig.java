package roomescape.config;

import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.common.exception.custom.PaymentClientException;
import roomescape.common.exception.custom.PaymentServerException;
import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.error.ClientErrorCode;
import roomescape.payment.error.InternalServerErrorCode;
import roomescape.payment.gateway.PaymentGateway;
import roomescape.payment.gateway.PaymentGatewayResolver;

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
                if (request.paymentKey().equals("server-error-key")) {
                    throw new PaymentServerException(InternalServerErrorCode.UNAUTHORIZED_KEY.getMessage());
                }
                if (request.paymentKey().equals("already-processed-key")) {
                    throw new PaymentClientException(ClientErrorCode.ALREADY_PROCESSED_PAYMENT.getMessage());
                }
            }
        };
    }

    @Bean
    public PaymentGatewayResolver resolver(List<PaymentGateway> gateways) {
        return new PaymentGatewayResolver(gateways);
    }
}
