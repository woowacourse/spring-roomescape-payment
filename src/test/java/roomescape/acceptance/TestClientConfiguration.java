package roomescape.acceptance;

import org.mockito.BDDMockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.payment.pg.TossPaymentsClient;
import roomescape.payment.pg.TossPaymentsPayment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestClientConfiguration {
    @Bean
    public TossPaymentsClient tossPaymentsClient() {
        TossPaymentsClient client = mock(TossPaymentsClient.class);
        TossPaymentsPayment payment = mock(TossPaymentsPayment.class);
        BDDMockito.when(payment.verify(any()))
                .thenReturn(true);
        BDDMockito.when(client.findBy(any()))
                .thenReturn(payment);

        return client;
    }
}
