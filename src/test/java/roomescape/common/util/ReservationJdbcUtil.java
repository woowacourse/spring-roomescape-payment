package roomescape.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationJdbcUtil {

    private final JdbcTemplate jdbcTemplate;

    public ReservationJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveSuccessReservationAsDateNow() {
        String sql = "insert into reservation (member_id, date, theme_id, time_id, status) values (1, CURRENT_DATE, 1, 1, 'SUCCESS')";

        jdbcTemplate.update(sql);
    }

    public void saveWaitReservationAsDateNow() {
        String sql = "insert into reservation (member_id, date, theme_id, time_id, status) values (1, CURRENT_DATE, 1, 1, 'WAIT')";

        jdbcTemplate.update(sql);
    }
}
