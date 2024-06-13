package roomescape.theme.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Optional;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Embeddable
public record ThemeDescription(
        @Column(name = "description", length = 255, nullable = false)
        String description) {
    private static final int MAX_LENGTH = 255;

    public ThemeDescription {
        Optional.ofNullable(description).orElseThrow(() ->
                new RoomEscapeException("테마 설명은 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));

        if (description.isEmpty() || description.length() > MAX_LENGTH) {
            throw new RoomEscapeException(
                    "테마 설명은 1글자 이상 255글자 이하이어야 합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }
}
