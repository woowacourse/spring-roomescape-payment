package roomescape.paymenthistory.domain;

import roomescape.paymenthistory.exception.PaymentException.PaymentServerError;

public class SecretKey {

    private final String secretKey;

    public SecretKey(String secretKey) {
        this.secretKey = secretKey;
        validation();
    }

    public String getSecretKey() {
        return secretKey;
    }

    private void validation() {
        if (secretKey == null) {
            throw new PaymentServerError();
        }
    }
}
