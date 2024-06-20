package roomescape.fixture;

import java.util.List;

import roomescape.domain.theme.Theme;

public class ThemeFixture {

    public static final List<Theme> THEMES = List.of(
            new Theme(1L, "테마1", "테마설명1", "thumbnail_1.jpg"),
            new Theme(2L, "테마2", "테마설명2", "thumbnail_2.jpg"),
            new Theme(3L, "테마3", "테마설명3", "thumbnail_3.jpg"),
            new Theme(4L, "테마4", "테마설명4", "thumbnail_4.jpg")
    );

    public static Theme themeFixture(int id) {
        return THEMES.get(id - 1);
    }
}
