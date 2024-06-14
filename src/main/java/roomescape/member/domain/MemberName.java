package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Optional;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Embeddable
public record MemberName(
        @Column(name = "name", length = 20, nullable = false, unique = true)
        String name) {
    private static final int MAX_LENGTH = 20;

    public MemberName {
        Optional.ofNullable(name).orElseThrow(() ->
                new RoomEscapeException("사용자 이름은 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));

        if (name.isEmpty() || name.length() > MAX_LENGTH) {
            throw new RoomEscapeException(
                    "예약자 이름은 1글자 이상 20글자 이하이어야 합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }
}
