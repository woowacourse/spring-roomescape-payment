package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "테마 생성 요청 DTO")
public record ThemeCreateRequest(
        @Schema(description = "테마 이름", example = "공포의 방탈출")
        @NotBlank(message = "테마 이름은 필수입니다.")
        String name,

        @Schema(description = "테마 설명", example = "무서운 공포 테마입니다.")
        @NotBlank(message = "테마 설명은 필수입니다.")
        String description,

        @Schema(description = "테마 썸네일 URL", example = "https://example.com/thumbnail.jpg")
        @NotBlank(message = "테마 썸네일은 필수입니다.")
        String thumbnail
) {
}
