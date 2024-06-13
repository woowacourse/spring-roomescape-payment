package roomescape.acceptance;

import org.mockito.BDDMockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.payment.application.PaymentGateway;
import roomescape.payment.domain.PaymentProduct;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static roomescape.TestFixture.SIMPLE_PAYMENT;

@TestConfiguration
public class TestPaymentConfiguration {
    @Bean
    public PaymentGateway paymentGateway() {
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        BDDMockito.when(paymentGateway.createPayment(any(), any()))
                .thenAnswer(invocation -> {
                    String paymentKey = invocation.getArgument(0);
                    PaymentProduct paymentProduct = invocation.getArgument(1);
                    return SIMPLE_PAYMENT(paymentKey, "order-" + paymentProduct.getProductId(), paymentProduct);
                });

        return paymentGateway;
    }
}
