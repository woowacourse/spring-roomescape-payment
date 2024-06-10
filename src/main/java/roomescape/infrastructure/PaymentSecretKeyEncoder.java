package roomescape.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentSecretKeyEncoder {
    @Value("${secret.key.payment}")
    private String WIDGET_SECRET_KEY;

    public String getEncodedSecretKey() {
        final Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(
                (WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

        return "Basic " + new String(encodedBytes);
    }
}
