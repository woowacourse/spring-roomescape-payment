package roomescape.fixture;

import roomescape.domain.reservationdetail.Theme;

public enum ThemeFixture {
    THEME_DREAM("꿈나라", "잠에서 깨기", "dream.jpg"),
    THEME_BED("침대", "침대 탈출하기", "bed.jpg"),
    THEME_DATABASE("데이터베이스", "데이터베이스를 공부하자", "database.jpg"),
    THEME_JAVA("자바", "자바는 어려워", "java.jpg");

    private final String name;
    private final String description;
    private final String thumbnail;

    ThemeFixture(String name, String description, String thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public Theme create() {
        return new Theme(name, description, thumbnail);
    }
}
