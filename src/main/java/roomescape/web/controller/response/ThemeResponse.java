package roomescape.web.controller.response;

import roomescape.service.response.ThemeDto;

public record ThemeResponse(Long id, String name, String description, String thumbnail) {

    public ThemeResponse(ThemeDto themeDto) {
        this(
                themeDto.id(),
                themeDto.name(),
                themeDto.description(),
                themeDto.thumbnail()
        );
    }
}
