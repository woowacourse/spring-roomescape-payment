package roomescape.presentation.dto.response;

import roomescape.business.model.entity.Theme;

public record ThemeResponse(
        String id,
        String name,
        String description,
        String thumbnail,
        Long price
) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId().value(), theme.getName().value(), theme.getDescription(),
                theme.getThumbnail(), theme.getPrice());
    }
}
