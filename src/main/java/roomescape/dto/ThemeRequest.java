package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ThemeRequest(
        @Schema(description = "테마 이름")
        String name,

        @Schema(description = "테마 설명")
        String description,

        @Schema(description = "썸네일 이미지 url")
        String thumbnail
) {
}
