package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

@Schema(description = "테마 순위 응답")
public record ThemeRankResponse(

        @Schema(description = "테마 이름", example = "홍길동전")
        String name,

        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail,

        @Schema(description = "테마 설명", example = "홍길동과 함께 떠나는 모험")
        String description) {

    public static ThemeRankResponse fromTheme(Theme theme) {
        return new ThemeRankResponse(theme.getName(), theme.getThumbnail(), theme.getDescription());
    }
}
