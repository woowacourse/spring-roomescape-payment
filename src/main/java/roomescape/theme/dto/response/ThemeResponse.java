package roomescape.theme.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

@Schema(description = "테마 응답")
public record ThemeResponse(
        @Schema(description = "테마 ID", example = "1")
        Long id,
        @Schema(description = "테마 이름", example = "기본 테마")
        String name,
        @Schema(description = "테마 설명", example = "기본 테마입니다.")
        String description,
        @Schema(description = "테마 썸네일", example = "https://example.com/thumbnail.jpg")
        String thumbnail) {
    public static ThemeResponse from(Theme findTheme) {
        return new ThemeResponse(findTheme.getId(), findTheme.getName(), findTheme.getDescription(),
                findTheme.getThumbnail());
    }
}
