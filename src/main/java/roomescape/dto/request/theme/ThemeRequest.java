package roomescape.dto.request.theme;

import jakarta.validation.constraints.NotBlank;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;

public record ThemeRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String thumbnail,
        @NotBlank int price
) {
    public Theme toTheme() {
        return new Theme(new ThemeName(name), description, thumbnail, price);
    }
}
