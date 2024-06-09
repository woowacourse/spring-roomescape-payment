package roomescape.view.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserPageControllerTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("유저 페이지를 열 수 있다.")
    @ParameterizedTest
    @CsvSource({"/", "/reservation", "/login", "/reservation-mine", "/payment"})
    void loadUserPages(String uri) {
        RestAssured.given().log().all()
                .when().get(uri)
                .then().log().all()
                .statusCode(200);
    }
}
