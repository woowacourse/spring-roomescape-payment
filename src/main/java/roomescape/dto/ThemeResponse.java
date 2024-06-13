package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Theme;

@Schema(description = "테마 응답 DTO")
public record ThemeResponse(@Schema(example = "1") long id,
                            @Schema(description = "테마 이름", example = "멸종") String name,
                            @Schema(description = "테마 설명", example = "우린 아무것도 될 수 없었네") String description,
                            @Schema(description = "테마 썸네일", example = "thumbnail-url") String thumbnail) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
