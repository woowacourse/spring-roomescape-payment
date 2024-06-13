package roomescape.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.ReservationTime;

@Component
public class ReservationTimeJdbcUtil {

    private final JdbcTemplate jdbcTemplate;

    public ReservationTimeJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveReservationTime(ReservationTime time) {
        String sql = "insert into reservation_time (start_at) values (?)";

        jdbcTemplate.update(sql, time.getStartAt());
    }

    public void saveReservationTimeAsTen() {
        String sql = "insert into reservation_time (start_at) values ('10:00')";

        jdbcTemplate.update(sql);
    }
}
