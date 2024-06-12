package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import roomescape.domain.Role;
import roomescape.infrastructure.TokenGenerator;

import java.util.HashMap;
import java.util.Map;

import static roomescape.fixture.TestFixture.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {"spring.config.location=classpath:/application-test.yml"})
class WaitingControllerTest {

    @Autowired
    TokenGenerator tokenGenerator;

    private static final String EMAIL = "test@email.com";

    @LocalServerPort
    private int port;
    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        accessToken = tokenGenerator.createToken(EMAIL, Role.ADMIN.name());
    }

    @DisplayName("선택한 시간대와 테마에 이미 예약이 있으면 예약 대기를 걸 수 있다.")
    @Test
    void given_alreadyBooked_when_waiting_then_statusCodeIsCreated() {
        Map<String, String> params = new HashMap<>();
        params.put("date", "2099-04-30");
        params.put("timeId", "1");
        params.put("themeId", "1");

        RestAssured.given().log().all()
                .cookie(TOKEN, accessToken)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);
    }
}