package roomescape.theme.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateThemeRequest(
        @NotBlank
        @Schema(description = "테마 이름", example = "헤일러의 디버깅 교실")
        String name,
        @NotBlank
        @Schema(description = "테마 설명", example = "디버깅 실력이 쑥쑥")
        String description,
        @NotBlank
        @Schema(description = "썸네일 경로", example = "https://썸네일경로")
        String thumbnail
) {
}
