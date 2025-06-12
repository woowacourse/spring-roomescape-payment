package roomescape.global.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    INVALID_LOGIN_CREDENTIALS("이메일 또는 비밀번호가 일치하지 않습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
