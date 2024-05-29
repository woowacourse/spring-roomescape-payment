package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.reservation.domain.Description;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeName;

@NotNull
public record ThemeSaveRequest(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotBlank
        String thumbnail
) {

    public Theme toTheme() {
        return new Theme(new ThemeName(name), new Description(description), thumbnail);
    }
}
