package roomescape.infrastructure.auth;

import roomescape.exception.custom.RoomEscapeException;

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
            throw new RoomEscapeException("비밀번호가 없습니다.");
        }
    }
}
