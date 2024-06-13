package roomescape.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.Theme;

@Component
public class ThemeJdbcUtil {

    private final JdbcTemplate jdbcTemplate;

    public ThemeJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveTheme(Theme theme) {
        String sql = "insert into theme(name, description, thumbnail) values (?, ?, ?)";

        jdbcTemplate.update(sql, theme.getName(), theme.getDescription(), theme.getThumbnail());
    }

    public void saveThemeAsHorror() {
        String sql = "insert into theme(name, description, thumbnail) values ('공포', '무서운 테마', 'https://a.com/a.jpg')";

        jdbcTemplate.update(sql);
    }
}
