package roomescape.theme.application.dto;

import java.math.BigDecimal;
import java.util.List;
import roomescape.theme.domain.Theme;

public record ThemeResponse(Long id, String name, String description, String thumbnail, BigDecimal price) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail(),
                theme.getPrice());
    }

    public static List<ThemeResponse> from(List<Theme> themes) {
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }
}
