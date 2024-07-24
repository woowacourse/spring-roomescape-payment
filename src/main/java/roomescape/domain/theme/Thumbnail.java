package roomescape.domain.theme;

import jakarta.persistence.Embeddable;
import roomescape.exception.custom.RoomEscapeException;

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
                    "썸네일은 1자 이상 가능합니다.",
                    "theme_thumbnail : " + thumbnail
            );
        }
    }
}
