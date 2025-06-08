package roomescape.domain.theme.dto;

import roomescape.domain.theme.Theme;
import roomescape.exception.custom.reason.ResponseInvalidException;

public record ThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnail
) {
    public ThemeResponse {
        if (id == null || name == null || description == null || thumbnail == null) {
            throw new ResponseInvalidException();
        }
    }

    public static ThemeResponse from(final Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
