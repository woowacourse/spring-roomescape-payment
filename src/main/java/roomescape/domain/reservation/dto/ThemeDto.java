package roomescape.domain.reservation.dto;

import roomescape.domain.reservation.model.Theme;
import roomescape.domain.reservation.model.ThemeDescription;
import roomescape.domain.reservation.model.ThemeName;
import roomescape.domain.reservation.model.ThemeThumbnail;

public record ThemeDto(
        Long id,
        ThemeName name,
        ThemeDescription description,
        ThemeThumbnail thumbnail
) {
    public static ThemeDto from(final Theme theme) {
        return new ThemeDto(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
