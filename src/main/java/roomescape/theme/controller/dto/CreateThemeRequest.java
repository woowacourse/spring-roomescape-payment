package roomescape.theme.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "테마 생성 요청 정보")
public record CreateThemeRequest(

        @Schema(description = "테마 이름", example = "감옥에서 탈출하기")
        @NotBlank
        String name,

        @Schema(description = "테마 설명", example = "감옥에 갇힌 당신, 제한 시간 내에 탈출하라!")
        @NotBlank
        String description,

        @Schema(description = "테마 썸네일 URL", example = "https://example.com/image.jpg")
        @NotBlank
        String thumbnail

) {}
