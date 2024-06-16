package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.theme.domain.Theme;

@Tag(name = "테마 응답", description = "사용자 요청에 따른 테마 응답")
public record ThemeResponse(
        long id,
        String name,
        String description,
        String thumbnail
) {

    public static ThemeResponse fromTheme(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }

}
