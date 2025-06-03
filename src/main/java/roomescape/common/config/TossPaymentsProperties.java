package roomescape.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "pay.toss")
public class TossPaymentsProperties {
    private final String secretKey;
    private final String baseUrl;
    private final TimeoutProperties timeout;

    public int getConnectionTimeout() {
        return timeout.getConnection();
    }

    public int getReadTimeout() {
        return timeout.getRead();
    }
}
