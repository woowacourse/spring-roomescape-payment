package roomescape.payment;

import java.util.Base64;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("toss.payment")
public class TossPaymentProperties {

    private final String widgetSecretKey;
    private final String baseUrl;

    public TossPaymentProperties(String widgetSecretKey, String baseUrl) {
        this.widgetSecretKey = widgetSecretKey;
        this.baseUrl = baseUrl;
    }

    public String getEncodedSecretKey() {
        String preparedWidgetSecretKey = widgetSecretKey + ":";
        return Base64.getEncoder().encodeToString(preparedWidgetSecretKey.getBytes());
    }
}
