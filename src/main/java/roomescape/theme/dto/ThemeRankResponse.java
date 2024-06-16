package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.theme.domain.Theme;

@Tag(name = "테마 정보 응답", description = "테마 정보 응답")
public record ThemeRankResponse(
        String name,
        String thumbnail,
        String description
) {
    public static ThemeRankResponse fromTheme(Theme theme) {
        return new ThemeRankResponse(theme.getName(), theme.getThumbnail(), theme.getDescription());
    }
}
