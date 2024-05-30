package roomescape.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import roomescape.util.Base64Utils;

@ConfigurationProperties(prefix = "payment")
public class PaymentClientProperties {
    private static final String BASIC_AUTH_FORMAT = "Basic %s";

    private final String url;
    private final String secret;

    @ConstructorBinding
    public PaymentClientProperties(String url, String secret) {
        this.url = url;
        this.secret = String.format(
                BASIC_AUTH_FORMAT,
                Base64Utils.encode(secret + ":")
        );
    }

    public String getUrl() {
        return url;
    }

    public String getBasicKey() {
        return secret;
    }
}
