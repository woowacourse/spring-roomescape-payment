package roomescape.theme.dto;

import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

public record ThemeRequest(
        String name,
        String description,
        String thumbnail
) {
    public Theme toTheme() {
        return new Theme(new Name(name), description, thumbnail);
    }
}