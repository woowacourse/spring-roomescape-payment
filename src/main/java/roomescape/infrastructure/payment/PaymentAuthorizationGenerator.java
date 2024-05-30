package roomescape.infrastructure.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentAuthorizationGenerator {

    private static final Encoder ENCODER = Base64.getEncoder();

    private final String secretKey;

    public PaymentAuthorizationGenerator(@Value("${payment.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String createAuthorizations() {
        byte[] encodedBytes = ENCODER.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
