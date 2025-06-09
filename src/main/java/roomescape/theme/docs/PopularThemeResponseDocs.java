package roomescape.theme.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.dto.response.PopularThemeResponse;

@Schema(description = "인기 테마 응답")
public record PopularThemeResponseDocs(
        @Schema(description = "테마 이름", example = "기본 테마")
        String name,
        @Schema(description = "테마 썸네일", example = "https://example.com/thumbnail.jpg")
        String thumbnail,
        @Schema(description = "테마 설명", example = "기본 테마입니다.")
        String description) {

    public static PopularThemeResponseDocs from(PopularThemeResponse response) {
        return new PopularThemeResponseDocs(response.name(), response.thumbnail(), response.description());
    }
} 