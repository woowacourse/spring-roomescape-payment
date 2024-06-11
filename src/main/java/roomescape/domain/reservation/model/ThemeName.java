package roomescape.domain.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.domain.reservation.exception.InvalidReserveInputException;

@Embeddable
public class ThemeName {

    private static final int MAXIMUM_ENABLE_NAME_LENGTH = 20;

    @Column(length = 100, nullable = false)
    private String name;

    protected ThemeName() {
    }

    public ThemeName(final String name) {
        validateValue(name);
        this.name = name;
    }

    private void validateValue(final String value) {
        if (value == null) {
            throw new InvalidReserveInputException("테마 이름은 1글자 이상 20글자 이하여야 합니다.");
        }

        final String stripedValue = value.strip();
        if (stripedValue.isEmpty() || stripedValue.length() > MAXIMUM_ENABLE_NAME_LENGTH) {
            throw new InvalidReserveInputException("테마 이름은 1글자 이상 20글자 이하여야 합니다.");
        }
    }

    @Override
    public String toString() {
        return "ThemeName{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getValue() {
        return name;
    }
}
