package roomescape.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payments.toss")
public class TossPaymentSettings {

    private String hostName;
    private String createPaymentApi;
    private String cancelPaymentApi;
    private String secretKey;
    private String password;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public String getCreatePaymentApi() {
        return createPaymentApi;
    }

    public void setCreatePaymentApi(final String createPaymentApi) {
        this.createPaymentApi = createPaymentApi;
    }

    public String getCancelPaymentApi() {
        return cancelPaymentApi;
    }

    public void setCancelPaymentApi(final String cancelPaymentApi) {
        this.cancelPaymentApi = cancelPaymentApi;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
