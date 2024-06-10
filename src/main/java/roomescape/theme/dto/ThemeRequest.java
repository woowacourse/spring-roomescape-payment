package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.entity.Theme;

@Schema(description = "테마 요청")
public record ThemeRequest(
        @Schema(description = "테마 이름", defaultValue = "테마 이름") String name,
        @Schema(description = "테마 설명", defaultValue = "테마 설명") String description,
        @Schema(description = "테마 이미지", defaultValue = "테마 이미지") String thumbnail) {
    public Theme toTheme() {
        return new Theme(name, description, thumbnail);
    }
}
