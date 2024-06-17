package roomescape.payment.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private final String confirmSecretKey;
    private final int readTimeout;
    private final int connectTimeout;

    public PaymentProperties(String confirmSecretKey, int readTimeout, int connectTimeout) {
        this.confirmSecretKey = confirmSecretKey;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
    }

    public String getConfirmSecretKey() {
        return confirmSecretKey;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }
}
