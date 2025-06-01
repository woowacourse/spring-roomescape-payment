package roomescape.payment.infrastructure.toss.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import roomescape.common.config.ApiProperties;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "external.toss")
public class TossApiProperties extends ApiProperties {
    private String secretKey;
}
