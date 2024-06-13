package roomescape.infrastructure.auth;

import static roomescape.exception.ErrorCode.USER_PASSWORD_EMPTY_ERROR;

import roomescape.exception.RoomEscapeException;

public class PasswordEncoder {

    public String encode(String password) {
        validateEmpty(password);
        return String.valueOf(password.hashCode());
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        validateEmpty(rawPassword);
        String loginPassword = encode(rawPassword);
        return loginPassword.equals(encodedPassword);
    }

    private void validateEmpty(String password) {
        if (password == null || password.isEmpty()) {
            throw new RoomEscapeException(USER_PASSWORD_EMPTY_ERROR);
        }
    }
}
