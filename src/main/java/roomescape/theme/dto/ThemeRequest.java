package roomescape.theme.dto;

import roomescape.theme.entity.Theme;

public record ThemeRequest(String name, String description, String thumbnail) {
    public Theme toTheme() {
        return new Theme(name, description, thumbnail);
    }
}
