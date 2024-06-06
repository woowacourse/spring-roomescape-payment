package roomescape.acceptance;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.payment.pg.TossPaymentsClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestClientConfiguration {

    @Bean
    public TossPaymentsClient tossPaymentsClient() {
        return mock(TossPaymentsClient.class);
    }
}
