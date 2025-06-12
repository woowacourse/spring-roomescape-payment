package roomescape.dto.theme.response;

import roomescape.domain.theme.Theme;

public record ThemeResponse(Long id, String name, String description, String thumbnail) {

    public static ThemeResponse from(Theme findTheme) {
        return new ThemeResponse(findTheme.id(), findTheme.name(), findTheme.description(),
                findTheme.thumbnail());
    }
}
