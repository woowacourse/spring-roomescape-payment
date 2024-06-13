package roomescape.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.theme.Theme;

public record ThemeResponse(
        @Schema(description = "테마 고유 번호", example = "1")
        Long id,
        @Schema(description = "테마 이름", example = "공포의 우테코")
        String name,
        @Schema(description = "테마 설명", example = "여기는 어딘가, 공포 속에서 탈출하기")
        String description,
        @Schema(description = "테마 이미지", example = "image.png")
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
