package roomescape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import roomescape.payment.infrastructure.TossPaymentProperties;

@EnableRetry
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(TossPaymentProperties.class)
public class RoomescapeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RoomescapeApplication.class, args);
    }
}
