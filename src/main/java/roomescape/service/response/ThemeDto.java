package roomescape.service.response;

import roomescape.domain.Theme;

public record ThemeDto(Long id, String name, String description, String thumbnail) {

    public ThemeDto(Theme theme) {
        this(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
