package roomescape.auth.application.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidEmailException extends AuthenticationException {

    public InvalidEmailException(String email) {
        super("등록이 되지 않은 유저 이메일 입니다.");
        log.error("인증 실패: 존재하지 않는 이메일 - {}", email);
    }
}
