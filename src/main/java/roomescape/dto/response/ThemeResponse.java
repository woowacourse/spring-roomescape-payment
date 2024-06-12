package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Theme;

@Schema(description = "Theme Response Model")
public record ThemeResponse(@Schema(description = "Theme ID", example = "123")
                            Long id,

                            @Schema(description = "Theme name", example = "Armageddon")
                            String name,

                            @Schema(description = "Theme description", example = "Meet the prettiest girls in Korea")
                            String description,

                            @Schema(description = "Thumbnail URL of the theme", example = "http://example.com/thumbnail.jpg")
                            String thumbnail) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
