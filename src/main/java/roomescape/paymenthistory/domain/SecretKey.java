package roomescape.paymenthistory.domain;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import roomescape.paymenthistory.exception.PaymentException.PaymentServerError;

public class SecretKey {

    private static final String BASIC = "Basic ";
    private static final String Delimiter = ":";

    private final String secretKey;

    public SecretKey(String secretKey) {
        validation(secretKey);
        this.secretKey = new String(
                Base64.getEncoder().encode((secretKey + Delimiter).getBytes(StandardCharsets.UTF_8)));
    }

    public String makeAuthorization() {
        return BASIC + secretKey;
    }

    private void validation(String secretKey) {
        if (secretKey == null) {
            throw new PaymentServerError();
        }
    }
}
