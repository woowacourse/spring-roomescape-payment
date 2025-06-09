package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "테마 생성 요청 객체")
public record ThemeRequest(
        @NotBlank
        @Schema(description = "이름", example = "재미있는 테마")
        String name,

        @NotBlank
        @Schema(description = "설명", example = "아주 재미있는 테마입니다.")
        String description,

        @NotBlank
        @Schema(description = "썸네일", example = "thumbnail.com")
        String thumbnail
) {
}
