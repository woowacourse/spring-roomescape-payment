package roomescape.service.theme.dto;

import jakarta.validation.constraints.NotBlank;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;

public class ThemeRequest {
    @NotBlank(message = "name 값이 null 또는 공백일 수 없습니다.")
    private final String name;
    @NotBlank(message = "description 값이 null 또는 공백일 수 없습니다.")
    private final String description;
    @NotBlank(message = "thumbnail 값이 null 또는 공백일 수 없습니다.")
    private final String thumbnail;

    public ThemeRequest(String name, String description, String thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public Theme toTheme() {
        return new Theme(new ThemeName(name), description, thumbnail);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
