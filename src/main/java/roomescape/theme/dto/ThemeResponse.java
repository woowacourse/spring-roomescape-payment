package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

@Schema(description = "테마 응답")
public record ThemeResponse(
        @Schema(description = "테마 ID", example = "1")
        long id,

        @Schema(description = "테마 이름", example = "홍길동전")
        String name,

        @Schema(description = "테마 설명", example = "조선시대로의 모험")
        String description,

        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail,

        @Schema(description = "테마 가격", example = "30000")
        Long price) {

    public static ThemeResponse fromTheme(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail(), theme.getPrice());
    }
}
