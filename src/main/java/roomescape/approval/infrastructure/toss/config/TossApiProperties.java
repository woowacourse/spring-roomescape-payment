package roomescape.approval.infrastructure.toss.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import roomescape.common.config.ApiProperties;

@Getter
@ConfigurationProperties(prefix = "external.toss")
public class TossApiProperties extends ApiProperties {
    private final String secretKey;

    public TossApiProperties(String baseUrl, int connectTimeout, int readTimeout, String secretKey) {
        super(baseUrl, connectTimeout, readTimeout);
        this.secretKey = secretKey;
    }
}
