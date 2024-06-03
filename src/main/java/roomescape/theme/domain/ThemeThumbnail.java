package roomescape.theme.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Embeddable
public record ThemeThumbnail(
        @Column(name = "thumbnail", length = 500, nullable = false)
        String thumbnail) {
    private static final int MAX_LENGTH = 500;

    public ThemeThumbnail {
        Objects.requireNonNull(thumbnail);
        if (thumbnail.isEmpty() || thumbnail.length() > MAX_LENGTH) {
            throw new RoomEscapeException(
                    "테마 썸네일은 1글자 이상 500글자 이하이어야 합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }
}
