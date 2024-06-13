package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.Theme;

@Schema(description = "테마 응답 DTO 입니다.")
public record ThemeResponse(
        @Schema(description = "테마 ID 입니다.")
        long id,
        @Schema(description = "테마명입니다.")
        String name,
        @Schema(description = "테마 설명입니다.")
        String description,
        @Schema(description = "테마 이미지 링크 주소입니다.")
        String thumbnail
) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
