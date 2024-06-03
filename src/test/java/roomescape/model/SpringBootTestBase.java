package roomescape.model;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import roomescape.fixture.RestAssuredTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(RestAssuredTemplate.class)
public abstract class SpringBootTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    protected RestAssuredTemplate restAssuredTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

}
