package roomescape.domain.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.theme.entity.Theme;

@Schema(description = "테마 응답 DTO")
public record ThemeResponse(
        @Schema(description = "테마 ID", example = "1")
        Long id,

        @Schema(description = "테마 이름", example = "미스터리 방탈출")
        String name,

        @Schema(description = "테마 설명", example = "미스터리 방탈출은 다양한 퍼즐과 수수께끼를 해결하는 테마입니다.")
        String description,

        @Schema(description = "테마 썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail
) {

    public static ThemeResponse from(final Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
