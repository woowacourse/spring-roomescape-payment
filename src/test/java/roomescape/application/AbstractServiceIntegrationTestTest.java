package roomescape.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractServiceIntegrationTestTest extends AbstractServiceIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ParameterizedTest
    @ValueSource(strings = {"member", "reservation", "reservation_time", "theme", "waiting"})
    void 서비스_통합_테스트_메서드를_실행할_떄마다_모든_테이블은_초기화된다(final String tableName) {
        // given
        final String query = "SELECT COUNT(*) FROM %s".formatted(tableName.toUpperCase());

        // when
        final Integer count = jdbcTemplate.queryForObject(query, Integer.class);

        // then
        assertThat(count)
                .isNotNull()
                .isZero();
    }

    @Test
    void 서비스_통합_테스트_메서드를_실행할_떄마다_모든_테이블의_auto_increment는_초기화된다() {
        // given
        final String insertQuery = "INSERT INTO reservation_time (id, start_at) VALUES (1, '13:00')";
        jdbcTemplate.update(insertQuery);

        // when
        final String selectQuery = "SELECT id FROM reservation_time WHERE start_at = '13:00'";
        final Long id = jdbcTemplate.queryForObject(selectQuery, Long.class);

        // then
        assertThat(id).isEqualTo(1L);
    }
}
