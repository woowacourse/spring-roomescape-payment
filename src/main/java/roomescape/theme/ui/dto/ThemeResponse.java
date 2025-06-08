package roomescape.theme.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

public record ThemeResponse(
        @Schema(description = "테마 id", example = "1")
        Long id,
        @Schema(description = "테마 이름", example = "헤일러의 디버깅 교실")
        String name,
        @Schema(description = "테마 설명", example = "디버깅 실력이 쑥쑥")
        String description,
        @Schema(description = "썸네일 경로", example = "https://썸네일경로")
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
