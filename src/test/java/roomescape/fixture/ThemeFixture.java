package roomescape.fixture;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import roomescape.domain.Theme;

@Component
public class ThemeFixture {

    public static Theme THEME_ONE = new Theme(1L, "레벨1 탈출", "우테코 레벨2를 탈출하는 내용입니다",
            "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg", BigDecimal.valueOf(10000));

    public static Theme THEME_TWO = new Theme(2L, "레벨2 탈출", "우테코 레벨3를 탈출하는 내용입니다",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg", BigDecimal.valueOf(15000));
}
