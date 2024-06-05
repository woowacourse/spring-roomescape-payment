package roomescape.payment.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private final String baseUrl;
    private final String encodedSecretKey;

    public PaymentProperties(String baseUrl, String secretKey) {
        this.baseUrl = baseUrl;
        this.encodedSecretKey = encodeSecretKey(secretKey);
    }

    private String encodeSecretKey(String secretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getEncodedSecretKey() {
        return encodedSecretKey;
    }
}
