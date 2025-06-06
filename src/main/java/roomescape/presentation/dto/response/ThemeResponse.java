package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Theme;

import java.util.List;

public record ThemeResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "우테코 레벨1 탈출")
        String name,

        @Schema(example = "우테코 레벨1 탈출 설명")
        String description,

        @Schema(example = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg")
        String thumbnail
) {

    public static List<ThemeResponse> from(List<Theme> themes) {
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
