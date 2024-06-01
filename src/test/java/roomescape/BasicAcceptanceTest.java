package roomescape;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.client.PaymentClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/reset.sql")
public abstract class BasicAcceptanceTest {
    @LocalServerPort
    private int port;

    @MockBean
    protected PaymentClient paymentClient;

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
    }
}
