package roomescape.domain.member;

import jakarta.persistence.Embeddable;
import roomescape.exception.custom.RoomEscapeException;

@Embeddable
public class Password {

    private static final int MIN_PASSWORD_LENGTH = 4;

    private String password;

    protected Password() {
    }

    public Password(String password) {
        validatePassword(password);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    private void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new RoomEscapeException(
                    "비밀번호는 4자 이상만 가능합니다.",
                    "password_length : " + password.length());
        }
    }
}
