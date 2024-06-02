package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment.toss")
public class PaymentProperties {
    private String secretKey;
    private String baseUrl;
    private int connectionTime;
    private int readTime;

    public String getSecretKey() {
        return secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }
}
