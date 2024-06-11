package roomescape.domain.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.domain.reservation.exception.InvalidReserveInputException;

@Embeddable
public class ThemeDescription {

    private static final int MAXIMUM_ENABLE_NAME_LENGTH = 80;

    @Column(length = 150, nullable = false)
    private String description;

    protected ThemeDescription() {
    }

    public ThemeDescription(final String description) {
        validateValue((description));
        this.description = description;
    }

    private void validateValue(final String value) {
        if (value == null) {
            throw new InvalidReserveInputException("테마 설명은 1글자 이상 80글자 이하여야 합니다.");
        }

        final String stripedValue = value.strip();
        if (stripedValue.isEmpty() || stripedValue.length() > MAXIMUM_ENABLE_NAME_LENGTH) {
            throw new InvalidReserveInputException("테마 설명은 1글자 이상 80글자 이하여야 합니다.");
        }
    }

    @Override
    public String toString() {
        return "ThemeDescription{" +
                "description='" + description + '\'' +
                '}';
    }

    public String getValue() {
        return description;
    }
}
