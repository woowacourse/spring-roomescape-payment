package roomescape.acceptance;

import org.mockito.BDDMockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.payment.pg.TossPaymentsClient;
import roomescape.payment.pg.TossPaymentsPayment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static roomescape.TestFixture.PAYMENT_CONFIRM_REQUEST;

@TestConfiguration
public class TestClientConfiguration {
    @Bean
    public TossPaymentsClient tossPaymentsClient() {
        TossPaymentsClient client = mock(TossPaymentsClient.class);
        TossPaymentsPayment payment = new TossPaymentsPayment(
                PAYMENT_CONFIRM_REQUEST.paymentKey(),
                PAYMENT_CONFIRM_REQUEST.orderId(),
                "IN_PROGRESS",
                PAYMENT_CONFIRM_REQUEST.amount());
        BDDMockito.when(client.findBy(any()))
                .thenReturn(payment);

        return client;
    }
}
