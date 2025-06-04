package roomescape.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Base64;

@Embeddable
public record Password(
        @Column(name = "password", nullable = false)
        String value
) {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    public static Password encode(String rawPassword) {
        String encodedValue = ENCODER.encodeToString(rawPassword.getBytes());
        return new Password(encodedValue);
    }

    public boolean matches(String rawPassword) {
        return encode(rawPassword).equals(this);
    }
}
