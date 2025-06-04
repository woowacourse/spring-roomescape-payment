package roomescape.reservation.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import roomescape.reservation.infrastructure.TossClientProperties;

@Component
@EnableConfigurationProperties(TossClientProperties.class)
public class TossClientConfiguration {
}
