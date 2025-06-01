package roomescape.payment.infrastructure.toss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import roomescape.common.config.ApiProperties;

@Component
@ConfigurationProperties(prefix = "external.toss")
public class TossApiProperties extends ApiProperties {
}
