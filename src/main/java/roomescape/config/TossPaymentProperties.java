package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@ConfigurationProperties(prefix = "third-party-api.toss-payment")
public class TossPaymentProperties {

    private final String url;
    private final String path;
    private final String confirmPath;
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final String secretKey;

    public TossPaymentProperties(String url, String path, String confirmPath, Duration connectTimeout, Duration readTimeout, String secretKey) {
        this.url = url;
        this.path = path;
        this.confirmPath = confirmPath;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.secretKey = secretKey;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getConfirmPath() {
        return confirmPath;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
