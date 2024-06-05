package roomescape.theme.dto;

import roomescape.theme.domain.Theme;

public record ThemeResponse(
        long id,
        String name,
        String description,
        String thumbnail,
        Long price
) {

    public static ThemeResponse fromTheme(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail(), theme.getPrice());
    }
}
