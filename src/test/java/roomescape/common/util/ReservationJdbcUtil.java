package roomescape.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationJdbcUtil {

    private final JdbcTemplate jdbcTemplate;

    public ReservationJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveReservationAsDateNow() {
        String sql = "insert into reservation (member_id, date, theme_id, time_id) values (1, CURRENT_DATE, 1, 1)";

        jdbcTemplate.update(sql);
    }
}
