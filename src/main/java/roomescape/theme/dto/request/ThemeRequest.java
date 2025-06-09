package roomescape.theme.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "테마 요청")
public record ThemeRequest(
        @Schema(description = "테마 이름", example = "기본 테마")
        String name,
        @Schema(description = "테마 설명", example = "기본 테마입니다.")
        String description,
        @Schema(description = "테마 썸네일", example = "https://example.com/thumbnail.jpg")
        String thumbnail) {
    public ThemeRequest {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("테마 이름이 비어있을 수 없습니다.");
        }

        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("테마 설명이 비어있을 수 없습니다.");
        }

        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new IllegalArgumentException("테마 썸네일이 비어있을 수 없습니다.");
        }
    }
}
