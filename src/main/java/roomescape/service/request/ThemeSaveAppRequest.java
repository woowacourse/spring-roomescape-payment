package roomescape.service.request;

import roomescape.web.controller.request.ThemeRequest;

public record ThemeSaveAppRequest(String name, String description, String thumbnail) {

    public static ThemeSaveAppRequest from(ThemeRequest request) {
        return new ThemeSaveAppRequest(request.name(), request.description(), request.thumbnail());
    }
}
