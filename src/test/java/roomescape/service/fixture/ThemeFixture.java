package roomescape.service.fixture;

import roomescape.model.Theme;

public enum ThemeFixture {
    GENERAL(1L, "name", "description", "thumbnail");

    private Long id;
    private String name;
    private String description;
    private String thumbnail;

    ThemeFixture(final Long id, final String name, final String description, final String thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public Theme getTheme() {
        return new Theme(id, name, description, thumbnail);
    }
}
