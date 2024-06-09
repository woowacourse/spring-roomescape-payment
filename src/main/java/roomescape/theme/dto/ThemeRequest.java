package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "테마 저장 요청", description = "테마 정보를 저장할 때 사용합니다.")
public record ThemeRequest(
        @NotBlank(message = "테마의 이름은 null 또는 공백일 수 없습니다.")
        @Size(min = 1, max = 20, message = "테마의 이름은 1~20글자 사이여야 합니다.")
        @Schema(description = "필수 값이며, 최대 20글자까지 입력 가능합니다.")
        String name,
        @NotBlank(message = "테마의 설명은 null 또는 공백일 수 없습니다.")
        @Size(min = 1, max = 100, message = "테마의 설명은 1~100글자 사이여야 합니다.")
        @Schema(description = "필수 값이며, 최대 100글자까지 입력 가능합니다.")
        String description,
        @NotBlank(message = "테마의 쌈네일은 null 또는 공백일 수 없습니다.")
        @Schema(description = "필수 값이며, 썸네일 이미지 URL 을 입력해주세요.")
        String thumbnail
) {
}
