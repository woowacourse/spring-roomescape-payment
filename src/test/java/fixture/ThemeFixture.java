package fixture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import roomescape.theme.entity.Theme;

public class ThemeFixture {

    public static Theme create(String name, String description, String url) {
        return new Theme(name, description, url);
    }

    public static Theme createDefault() {
        return create(createRandomString(20),
                createRandomString(20),
                createRandomString(20) + "jpg"
        );
    }

    public static List<Theme> createDefaultList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createDefault())
                .collect(Collectors.toList());
    }

    private static String createRandomString(int length) {
        return java.util.UUID.randomUUID().toString().substring(0, length);
    }
}
