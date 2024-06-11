package roomescape.domain.reservation.dto;

import roomescape.domain.reservation.model.Theme;

public record SaveThemeRequest(String name, String description, String thumbnail) {
    public Theme toModel() {
        return new Theme(name, description, thumbnail);
    }
}
