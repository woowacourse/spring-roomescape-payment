package roomescape.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentClientProperties {

    private String baseUrl;
    private String confirmApi;
    private String secretKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getConfirmApi() {
        return confirmApi;
    }

    public void setConfirmApi(final String confirmApi) {
        this.confirmApi = confirmApi;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }
}
