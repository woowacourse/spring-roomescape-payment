package roomescape.theme.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.dto.request.ThemeRequest;

@Schema(description = "테마 요청")
public record ThemeRequestDocs(
        @Schema(description = "테마 이름", example = "기본 테마")
        String name,
        @Schema(description = "테마 설명", example = "기본 테마입니다.")
        String description,
        @Schema(description = "테마 썸네일", example = "https://example.com/thumbnail.jpg")
        String thumbnail) {

    public ThemeRequest toThemeRequest() {
        return new ThemeRequest(name, description, thumbnail);
    }
} 