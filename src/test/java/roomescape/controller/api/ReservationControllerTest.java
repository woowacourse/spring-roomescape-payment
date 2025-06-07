package roomescape.controller.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.dto.auth.LoginRequest;
import roomescape.dto.auth.SignUpRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationControllerTest {

    @DisplayName("Reservation 목록 내용 갯수를 검사한다")
    @Test
    void reservationTest() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Nested
    @DisplayName("예약 삭제")
    class ReservationDeleteTest {

        String loginToken;

        @BeforeEach
        void setUp() {
            SignUpRequest signUpRequest = new SignUpRequest("가이온", "hello@woowa.com", "password");

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(signUpRequest)
                    .when().post("/members")
                    .then().log().all()
                    .statusCode(200);

            LoginRequest loginRequest = new LoginRequest("hello@woowa.com", "password");

            Map<String, String> cookies = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/login")
                    .getCookies();

            loginToken = cookies.get("token");
        }

        @DisplayName("존재하지 않는 예약을 삭제할 수 없다")
        @Test
        void invalidReservationIdDeleteTest() {
            RestAssured.given().cookie("token", loginToken).log().all()
                    .when().delete("/reservations/5")
                    .then().log().all()
                    .statusCode(404);
        }
    }
}
