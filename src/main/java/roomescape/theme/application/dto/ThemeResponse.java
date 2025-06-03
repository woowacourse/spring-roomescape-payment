package roomescape.theme.application.dto;

import java.math.BigDecimal;
import roomescape.theme.domain.Theme;

public record ThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnail,
        BigDecimal price
) {

    public static ThemeResponse from(final Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail(),
                theme.getPrice()
        );
    }
}
