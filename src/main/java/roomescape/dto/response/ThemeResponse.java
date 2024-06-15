package roomescape.dto.response;

import java.math.BigDecimal;
import roomescape.domain.Theme;

public record ThemeResponse(Long id, String name, String description, String thumbnail, BigDecimal price) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail(),
                theme.getPrice()
        );
    }
}
