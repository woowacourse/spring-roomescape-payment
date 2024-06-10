package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.entity.Theme;

@Schema(description = "테마 응답")
public record ThemeResponse(
        @Schema(description = "테마 ID", defaultValue = "1") long id,
        @Schema(description = "테마 이름", defaultValue = "테마 이름") String name,
        @Schema(description = "테마 설명", defaultValue = "테마 설명") String description,
        @Schema(description = "테마 썸네일", defaultValue = "테마 이미지") String thumbnail) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
