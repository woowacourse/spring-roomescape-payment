package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

public record ThemeResponse(
        @Schema(description = "테마 id", example = "1")
        Long id,
        @Schema(description = "테마 이름", example = "레벨 1 탈출")
        String name,
        @Schema(description = "테마 설명", example = "레벨 1을 탈출한다.")
        String description,
        @Schema(description = "테마 썸네일", example = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg")
        String thumbnail) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
