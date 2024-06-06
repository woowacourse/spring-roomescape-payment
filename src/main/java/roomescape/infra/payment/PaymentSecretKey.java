package roomescape.infra.payment;

import java.util.Base64;

public record PaymentSecretKey(String value) {
    public String value() {
        return Base64.getEncoder().encodeToString((value + ":").getBytes());
    }
}
