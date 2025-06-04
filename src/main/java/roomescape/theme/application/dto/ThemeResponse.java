package roomescape.theme.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import roomescape.theme.domain.Theme;

@Schema(description = "테마 응답")
public record ThemeResponse(
        @Schema(description = "테마 ID")
        Long id,

        @Schema(description = "테마 이름")
        String name,

        @Schema(description = "테마 설명")
        String description,

        @Schema(description = "썸네일 이미지 URL")
        String thumbnail
) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }

    public static List<ThemeResponse> from(List<Theme> themes) {
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }
}
