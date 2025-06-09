package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.member.exception.InvalidMemberException;

@Embeddable
public record Password(
        @Column(name = "password", nullable = false, length = 100)
        String value
) {
    private static final int MAX_LENGTH = 100;

    public static Password from(String password) {
        validate(password);
        return new Password(password);
    }

    private static void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidMemberException("비밀번호는 비어있을 수 없습니다.");
        }
        if (password.length() > MAX_LENGTH) {
            throw new InvalidMemberException("비밀번호는 100글자 이하여야 합니다.");
        }
    }
}
