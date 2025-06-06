package roomescape.mvc.theme.dto;

import roomescape.mvc.theme.request.ThemeCreationRequest;

public record ThemeCreationContent(String name, String description, String thumbnail) {

    public ThemeCreationContent(ThemeCreationRequest request) {
        this(request.name(), request.description(), request.thumbnail());
    }
}
