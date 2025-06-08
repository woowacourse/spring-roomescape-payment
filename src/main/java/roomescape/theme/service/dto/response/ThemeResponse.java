package roomescape.theme.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

@Schema(name = "ThemeResponse(테마 조회,생성 응답 DTO)")
public record ThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnail
) {

    public static ThemeResponse from(final Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
