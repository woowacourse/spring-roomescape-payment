package roomescape.payment.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toss.payment")
public class PaymentProperties {
    private int connectionTimeout;
    private int readTimeout;
    private String secretKey;

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
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

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
