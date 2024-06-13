package roomescape.core.dto.theme;

import roomescape.core.domain.Theme;

public record ThemeResponse(Long id, String name, String description, String thumbnail) {

    public static ThemeResponse from(final Theme theme) {
        final Long id = theme.getId();
        final String name = theme.getName();
        final String description = theme.getDescription();
        final String thumbnail = theme.getThumbnail();

        return new ThemeResponse(id, name, description, thumbnail);
    }
}
