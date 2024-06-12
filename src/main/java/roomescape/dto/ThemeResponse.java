package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Theme;

public record ThemeResponse(
        @Schema(description = "테마 id")
        long id,

        @Schema(description = "테마 이름")
        String name,

        @Schema(description = "테마 설명")
        String description,

        @Schema(description = "썸네일 이미지 url")
        String thumbnail
) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
