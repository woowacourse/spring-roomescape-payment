package roomescape.fixture;

import java.util.List;
import java.util.stream.IntStream;
import roomescape.domain.reservationdetail.Theme;

public class ThemeFixture {
    public static List<Theme> createThemes(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createTheme("테마" + i))
                .toList();
    }

    public static Theme createTheme(String name) {
        return new Theme(name, "테마 설명", "https://image.com/im.jpg");
    }
}
