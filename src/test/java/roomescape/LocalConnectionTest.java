package roomescape;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LocalConnectionTest {
    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("로컬 DB 커넥션을 얻는다.")
    void getConnection() {
        try (final Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertTableExists(connection, "RESERVATION");
            assertTableExists(connection, "RESERVATION_TIME");
            assertTableExists(connection, "THEME");
            assertTableExists(connection, "MEMBER");
            assertTableExists(connection, "PAYMENT");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertTableExists(final Connection connection, String tableName) throws SQLException {
        assertThat(connection.getMetaData().getTables(null, null, tableName, null).next()).isTrue();
    }
}
