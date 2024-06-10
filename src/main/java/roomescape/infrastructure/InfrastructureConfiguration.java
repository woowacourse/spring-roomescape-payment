package roomescape.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestClient.Builder;
import roomescape.domain.payment.PaymentClient;
import roomescape.infrastructure.payment.PaymentProperties;
import roomescape.infrastructure.payment.PaymentRestClientBuilder;
import roomescape.infrastructure.payment.toss.TossPaymentClient;
import roomescape.infrastructure.payment.toss.TossResponseErrorHandler;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@EnableConfigurationProperties(PaymentProperties.class)
public class InfrastructureConfiguration {
    private final PaymentProperties paymentProperties;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("reservation-scheduler-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public PaymentRestClientBuilder builder() {
        return new PaymentRestClientBuilder(paymentProperties);
    }

    @Bean
    public PaymentClient tossPaymentClient() {
        Builder toss = builder().generate("toss");
        return new TossPaymentClient(toss.build(), new TossResponseErrorHandler());
    }
}
