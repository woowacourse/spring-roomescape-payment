package roomescape.domain.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "테마 생성 요청 DTO")
public record ThemeRequest(

        @Schema(description = "테마 이름", example = "우가의 방탈출")
        String name,

        @Schema(description = "테마 설명", example = "우가의 방탈출은 재미있는 방탈출 게임입니다.")
        String description,

        @Schema(description = "테마 썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail
) {
}
