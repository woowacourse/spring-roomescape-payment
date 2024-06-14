package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Optional;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Embeddable
public record MemberPassword(
        @Column(name = "password", length = 100, nullable = false)
        String password) {
    private static final int MAX_LENGTH = 100;

    public MemberPassword {
        Optional.ofNullable(password).orElseThrow(
                () -> new RoomEscapeException("사용자 비밀번호는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));

        if (password.isEmpty() || password.length() > MAX_LENGTH) {
            throw new RoomEscapeException(
                    "사용자 비밀번호는 1글자 이상 100글자 이하이어야 합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }
}
