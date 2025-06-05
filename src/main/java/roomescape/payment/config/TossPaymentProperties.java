package roomescape.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public class TossPaymentProperties {

    private final String baseUrl;
    private final String secretKey;
    private final int connectTimeout;
    private final int readTimeout;

    public TossPaymentProperties(
        final String baseUrl,
        final String secretKey,
        final int connectTimeout,
        final int readTimeout
    ) {
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }
}
