package roomescape.fixture;

import roomescape.domain.theme.Theme;
import roomescape.service.theme.dto.ThemeRequest;

public class ThemeFixture {
    public static Theme createTheme() {
        return new Theme("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
    }

    public static ThemeRequest createThemeRequest() {
        return new ThemeRequest("레벨3 탈출", "우테코 레벨3를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
    }

    public static ThemeRequest createThemeRequest(String name) {
        return new ThemeRequest(name, name + "를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
    }
}
