package roomescape.service.response;

import roomescape.domain.Theme;

public record ThemeDto(Long id, String name, String description, String thumbnail, Long price) {

    public ThemeDto(Theme theme) {
        this(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail(), theme.getPrice());
    }
}
