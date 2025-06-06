package roomescape.business.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.exception.reservation.ThemeNameLengthException;

@Embeddable
public record ThemeName(
        @Column(name = "name")
        String value
) {
    private static final int MAX_LENGTH = 20;

    public ThemeName {
        validateMaxLength(value);
    }

    private static void validateMaxLength(final String name) {
        if (name.length() > MAX_LENGTH) {
            throw new ThemeNameLengthException(MAX_LENGTH);
        }
    }
}
