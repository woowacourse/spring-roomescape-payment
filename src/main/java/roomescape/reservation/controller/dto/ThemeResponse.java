package roomescape.reservation.controller.dto;

import java.math.BigDecimal;
import roomescape.reservation.domain.Theme;

public record ThemeResponse(long id, String name, String description, String thumbnail, BigDecimal price) {
    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail(), theme.getPrice());
    }
}
