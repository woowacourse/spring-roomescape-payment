package roomescape.common.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.common.security.application.MyPasswordEncoder;
import roomescape.reservation.application.event.TestEventPublisher;

@Configuration
public class TestConfig {

    @Bean
    public MyPasswordEncoder myPasswordEncoder() {
        return new MyPasswordEncoder();
    }

    @Bean
    public ApplicationEventPublisher applicationEventPublisher() {
        return new TestEventPublisher();
    }
}
