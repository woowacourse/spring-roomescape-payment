package roomescape.domain.member;

import static roomescape.exception.ErrorCode.USER_PASSWORD_LENGTH_ERROR;

import jakarta.persistence.Embeddable;
import roomescape.exception.RoomEscapeException;

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
                    USER_PASSWORD_LENGTH_ERROR,
                    "password_length = " + password.length());
        }
    }
}
