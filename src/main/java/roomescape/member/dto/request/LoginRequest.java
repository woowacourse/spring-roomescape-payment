package roomescape.member.dto.request;

import roomescape.common.exception.InvalidReservationException;

public record LoginRequest(String email, String password) {
    public LoginRequest {
        if (email == null || email.isBlank()) {
            throw new InvalidReservationException("이메일은 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidReservationException("비밀번호는 비어있을 수 없습니다.");
        }
    }
}
