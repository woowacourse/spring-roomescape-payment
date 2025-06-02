package roomescape.theme.controller.dto;

import java.util.List;
import roomescape.theme.domain.Theme;

public record ThemeResponse(Long id, String name, String description, String thumbnail) {

    public static List<ThemeResponse> from(final List<Theme> themes) {
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public static ThemeResponse from(final Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName().name(),
                theme.getDescription().description(),
                theme.getThumbnail().thumbnail()
        );
    }
}
