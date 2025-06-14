package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Theme;

@Schema(description = "테마 응답 객체")
public record ThemeResponse(
        @Schema(description = "테마 ID", example = "1")
        Long id,

        @Schema(description = "테마 이름", example = "Mystery of the Lost Temple")
        String name,

        @Schema(description = "테마 설명", example = "탈출 게임의 배경 및 스토리를 나타냅니다.")
        String description,

        @Schema(description = "테마 썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail
) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
