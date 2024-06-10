package roomescape.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentJdbcUtil {

    private final JdbcTemplate jdbcTemplate;

    public PaymentJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void savePayment() {
        String sql = "insert into payment (payment_key, order_id, amount, reservation_id) values ('5EnNZRJG', 'MC4wO', 10000, 1)";

        jdbcTemplate.update(sql);
    }
}
