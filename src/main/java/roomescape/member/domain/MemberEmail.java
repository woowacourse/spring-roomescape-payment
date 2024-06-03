package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Embeddable
public record MemberEmail(
        @Column(name = "email", length = 100, nullable = false, unique = true)
        String email) {
    private static final int MAX_LENGTH = 100;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[0-9a-zA-Z]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");

    public MemberEmail {
        Objects.requireNonNull(email);
        if (email.length() > MAX_LENGTH) {
            throw new RoomEscapeException(
                    "예약자 이메일은 1글자 이상 100글자 이하이어야 합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RoomEscapeException(
                    "이메일 형식이 일치하지 않습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }
}
