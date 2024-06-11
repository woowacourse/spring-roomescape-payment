package roomescape.domain.reservation.theme;

import jakarta.persistence.Embeddable;

@Embeddable
public class Thumbnail {

    private String thumbnail;

    public Thumbnail() {
    }

    public Thumbnail(String thumbnail) {
        validate(thumbnail);
        this.thumbnail = thumbnail;
    }

    private void validate(String thumbnail) {
        if (thumbnail == null || !thumbnail.startsWith("https://")) {
            throw new IllegalArgumentException("잘못된 형식의 썸네일 url입니다.");
        }
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
