package roomescape.domain.member;

import jakarta.persistence.Embeddable;
import roomescape.exception.member.MemberException;

@Embeddable
public record Email(String email) {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public Email {
        validateEmailIsNonBlank(email);
        validateEmailFormat(email);
    }

    private static void validateEmailIsNonBlank(final String email) {
        if (email == null || email.isEmpty()) {
            throw new MemberException("이메일은 비어있을 수 없습니다.");
        }
    }

    private static void validateEmailFormat(final String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new MemberException("이메일 형식이 맞지 않습니다.");
        }
    }
}
