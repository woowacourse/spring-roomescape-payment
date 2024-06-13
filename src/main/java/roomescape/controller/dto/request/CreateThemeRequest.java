package roomescape.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateThemeRequest(
        @Schema(description = "테마 이름", example = "공포의 우테코")
        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        @Length(min = 1, max = 50)
        String name,

        @Schema(description = "테마 설명", example = "여기는 어딘가, 공포 속에서 탈출하기")
        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        @Length(min = 1, max = 100)
        String description,

        @Schema(description = "테마 이미지", example = "image.png")
        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        @URL(message = "올바른 URL 형식이 아닙니다.")
        String thumbnail
) {
}
