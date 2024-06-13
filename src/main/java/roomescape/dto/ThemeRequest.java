package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.Theme;

@Schema(description = "테마 요청 DTO 입니다.")
public record ThemeRequest(
        @Schema(description = "테마명 입니다.")
        String name,
        @Schema(description = "테마 설명입니다.")
        String description,
        @Schema(description = "테마 이미지 링크 주소입니다.")
        String thumbnail
) {
    public Theme toTheme() {
        return new Theme(name, description, thumbnail);
    }
}
