package roomescape.domain.theme;

import jakarta.persistence.Embeddable;
import roomescape.exception.theme.InvalidThemeNameLengthException;

@Embeddable
public record ThemeName(String name) {
    private static final int MAX_LENGTH = 16;

    public ThemeName(String name) {
        this.name = name;
        validate(name);
    }

    private void validate(String name) {
        if (name.length() > MAX_LENGTH) {
            throw new InvalidThemeNameLengthException();
        }
    }
}