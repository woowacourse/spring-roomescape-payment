package roomescape.payment.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toss.payment")
public class PaymentProperties {
    private int connectionTimeout;
    private int readTimeout;
    private String baseUrl;
    private String authorizationPrefix;
    private String secretKey;

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthorizationPrefix() {
        return authorizationPrefix;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setAuthorizationPrefix(String authorizationPrefix) {
        this.authorizationPrefix = authorizationPrefix;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
