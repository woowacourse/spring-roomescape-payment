package roomescape.theme.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

public record ThemeWebResponse(
         @Schema(description = "테마 엔티티의 기본 키") Long id,
         @Schema(description = "테마의 이름") String name,
         @Schema(description = "테마의 설명") String description,
         @Schema(description = "테마의 썸네일 Url") String thumbnail
) {
    public static ThemeWebResponse from(final Theme theme) {
        return new ThemeWebResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
