package roomescape.application.reservation.dto.response;

import roomescape.domain.reservation.Theme;

public record ThemeResponse(long id, String name, String description, long price, String thumbnail) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getPrice(),
                theme.getThumbnailUrl()
        );
    }
}
