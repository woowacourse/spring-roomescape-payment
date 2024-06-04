package roomescape.service.theme.dto;

import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;
import roomescape.exception.common.InvalidRequestBodyException;

public class ThemeRequest {
    private final String name;
    private final String description;
    private final String thumbnail;

    public ThemeRequest(String name, String description, String thumbnail) {
        validate(name, description, thumbnail);
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    private void validate(String name, String description, String thumbnail) {
        if (name == null || name.isBlank() ||
                description == null || description.isBlank() ||
                thumbnail == null || thumbnail.isBlank()) {
            throw new InvalidRequestBodyException();
        }
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