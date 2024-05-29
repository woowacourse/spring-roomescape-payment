package roomescape.fixture;

import roomescape.reservation.domain.Theme;

import java.math.BigDecimal;

public class ThemeFixture {
    public static Theme getTheme1() {
        return new Theme(null, "theme1", "description", "thumbnail.png", BigDecimal.valueOf(15000L));
    }

    public static Theme getTheme2() {
        return new Theme(null, "theme2", "description2", "thumbnail.png", BigDecimal.valueOf(20000L));
    }
}
