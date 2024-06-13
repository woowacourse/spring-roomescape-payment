package roomescape.domain.theme;

import static roomescape.exception.ErrorCode.THEME_THUMBNAIL_LENGTH_ERROR;

import jakarta.persistence.Embeddable;
import roomescape.exception.RoomEscapeException;

@Embeddable
public class Thumbnail {

    private  String thumbnail;

    protected Thumbnail() {
    }

    public Thumbnail(String thumbnail) {
        validateThumbnail(thumbnail);
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    private void validateThumbnail(String thumbnail) {
        if (thumbnail.isEmpty()) {
            throw new RoomEscapeException(
                    THEME_THUMBNAIL_LENGTH_ERROR,
                    "theme_thumbnail = " + thumbnail
            );
        }
    }
}
