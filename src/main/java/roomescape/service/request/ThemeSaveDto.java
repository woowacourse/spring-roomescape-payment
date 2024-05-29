package roomescape.service.request;

import roomescape.web.controller.request.ThemeRequest;

public record ThemeSaveDto(String name, String description, String thumbnail) {

    public static ThemeSaveDto from(ThemeRequest request) {
        return new ThemeSaveDto(request.name(), request.description(), request.thumbnail());
    }
}
