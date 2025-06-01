package roomescape;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DBHelper dbHelper;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
    }

    @BeforeEach
    void cleanDatabase() {
        databaseCleaner.clean();
    }
}
