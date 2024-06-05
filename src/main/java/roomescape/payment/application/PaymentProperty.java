package roomescape.payment.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentProperty {

    private String name;
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private String secretKey;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
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
