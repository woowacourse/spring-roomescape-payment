package roomescape.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class WaitingJdbcUtil {

    private final JdbcTemplate jdbcTemplate;

    public WaitingJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveWaitAsDateNow() {
        String sql = "insert into waiting (member_id, date, theme_id, time_id, status) values (1, CURRENT_DATE, 1, 1, 'WAIT')";

        jdbcTemplate.update(sql);
    }
}
