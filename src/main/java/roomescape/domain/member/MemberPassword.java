package roomescape.domain.member;

import jakarta.persistence.Embeddable;
import roomescape.exception.member.InvalidMemberPasswordLengthException;

@Embeddable
public record MemberPassword(String password) {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 16;

    public MemberPassword {
        validate(password);
    }

    private void validate(String password) {
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            throw new InvalidMemberPasswordLengthException();
        }
    }
}
