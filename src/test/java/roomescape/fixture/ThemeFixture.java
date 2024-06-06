package roomescape.fixture;

import roomescape.application.reservation.dto.request.ThemeRequest;
import roomescape.domain.reservation.Theme;

public enum ThemeFixture {

    TEST_THEME("test", "test", "test.com", 10_000L),
    SCHOOL_THEME("school", "school theme", "school.com", 15_000L),
    SPACE_THEME("space", "space theme", "space.com", 20_000L),
    SPOOKY_THEME("spooky", "spooky theme", "spooky.com", 30_000L),
    FANTASY_THEME("fantasy", "fantasy theme", "fantasy.com", 40_000L),
    ;

    private final String name;
    private final String description;
    private final String thumbnail;
    private final long price;

    ThemeFixture(String name, String description, String thumbnail, long price) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public Theme create() {
        return new Theme(name, description, price, thumbnail);
    }

    public ThemeRequest request() {
        return new ThemeRequest(name, description, thumbnail, price);
    }
}
