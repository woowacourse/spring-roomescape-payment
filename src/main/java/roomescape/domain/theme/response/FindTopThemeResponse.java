package roomescape.domain.theme.response;

import roomescape.domain.theme.domain.Theme;

public record FindTopThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnail
) {

    public FindTopThemeResponse(Theme theme) {
        this(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
