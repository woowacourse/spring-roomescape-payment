package roomescape.mvc.theme.response;

import roomescape.mvc.theme.domain.Theme;

public record AddThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnail
) {

    public AddThemeResponse(Theme theme) {
        this(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}

