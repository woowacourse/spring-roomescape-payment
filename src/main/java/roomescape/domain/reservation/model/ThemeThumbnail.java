package roomescape.domain.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.domain.reservation.exception.InvalidReserveInputException;

@Embeddable
public class ThemeThumbnail {

    private static final int MAXIMUM_ENABLE_NAME_LENGTH = 700;

    @Column(length = 1000, nullable = false)
    private String thumbnail;

    protected ThemeThumbnail() {
    }

    public ThemeThumbnail(final String thumbnail) {
        validateValue(thumbnail);
        this.thumbnail = thumbnail;
    }

    private void validateValue(final String value) {
        if (value == null) {
            throw new InvalidReserveInputException("썸네일은 1글자 이상 700글자 이하여야 합니다.");
        }

        final String stripedValue = value.strip();
        if (stripedValue.isEmpty() || stripedValue.length() > MAXIMUM_ENABLE_NAME_LENGTH) {
            throw new InvalidReserveInputException("썸네일은 1글자 이상 700글자 이하여야 합니다.");
        }
    }

    @Override
    public String toString() {
        return "ThemeThumbnail{" +
                "thumbnail='" + thumbnail + '\'' +
                '}';
    }

    public String getValue() {
        return thumbnail;
    }
}
