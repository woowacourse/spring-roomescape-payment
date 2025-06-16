package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.member.exception.InvalidMemberException;

@Embeddable
public record Email(
        @Column(name = "email", nullable = false, unique = true, length = 100)
        String value
) {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final String AT = "@";

    public static Email from(String email) {
        validate(email);
        return new Email(email);
    }

    private static void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidMemberException("이메일은 비어있을 수 없습니다.");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidMemberException("올바른 이메일 형식이 아닙니다.");
        }
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new InvalidMemberException("이메일은 100글자 이하이어야합니다.");
        }
    }

    public String extractDomain() {
        if (value == null || !value.contains(AT)) {
            return "unknown";
        }
        return value.split(AT)[1].toLowerCase();
    }
}


