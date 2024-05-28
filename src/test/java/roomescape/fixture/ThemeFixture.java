package roomescape.fixture;

import roomescape.reservation.domain.Theme;

public class ThemeFixture {
    public static Theme getTheme1() {
        return new Theme(null, "theme1", "description", "thumbnail.png",15000L);
    }

    public static Theme getTheme2() {
        return new Theme(null, "theme2", "description2", "thumbnail.png",20000L);
    }
}
