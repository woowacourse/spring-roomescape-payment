package roomescape.domain.theme;

import static roomescape.exception.ErrorCode.THEME_NAME_LENGTH_ERROR;

import jakarta.persistence.Embeddable;
import roomescape.exception.RoomEscapeException;

@Embeddable
public class ThemeName {

    private static final int MAX_THEME_NAME_LENGTH = 20;

    private String themeName;

    protected ThemeName() {
    }

    public ThemeName(String themeName) {
        validateName(themeName);
        this.themeName = themeName;
    }

    public String getThemeName() {
        return themeName;
    }

    private void validateName(String name) {
        if (name.isEmpty() || name.length() > MAX_THEME_NAME_LENGTH) {
            throw new RoomEscapeException(
                    THEME_NAME_LENGTH_ERROR,
                    "theme_name = " + name
            );
        }
    }
}
