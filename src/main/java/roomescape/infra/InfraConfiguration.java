package roomescape.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.PaymentClient;
import roomescape.infra.payment.PaymentApiResponseErrorHandler;
import roomescape.infra.payment.TossPaymentClient;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class InfraConfiguration {
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("reservation-scheduler-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public PaymentClient paymentClient() {
        return new TossPaymentClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com").build(),
                new PaymentApiResponseErrorHandler(objectMapper)
        );
    }
}
