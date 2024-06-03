package roomescape.service.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {
    private String secretKey;
    private String url;

    public String getSecretKey() {
        return secretKey;
    }

    public String getUrl() {
        return url;
    }

    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
