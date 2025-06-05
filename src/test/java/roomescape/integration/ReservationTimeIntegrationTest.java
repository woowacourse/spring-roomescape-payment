package roomescape.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import roomescape.member.dto.LoginRequest;
import roomescape.payment.FakePaymentRestClientConfig;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(FakePaymentRestClientConfig.class)
@ActiveProfiles("test")
class ReservationTimeIntegrationTest {

    @LocalServerPort
    int port;

    String sessionId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        final LoginRequest loginRequest = new LoginRequest("admin@email.com", "1234");
        sessionId = RestAssured.given().contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/login")
                .then()
                .extract().cookie("JSESSIONID");
    }

    @Nested
    class FailureTest {
        @DisplayName("시간 포맷이 유효하지 않아 예외가 발생한다")
        @Test
        void reservationTimeAddTest() {
            Map<String, String> request = Map.of("startAt", "12;00");
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/times")
                    .then().log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @DisplayName("이미 예약이 된 시간을 삭제하여 예외가 발생한다")
        @Test
        void reservationTimeRemoveTest() {
            Map<String, Object> params = Map.of(
                    "memberId", "1",
                    "date", LocalDate.now().plusDays(1).toString(),
                    "themeId", "1",
                    "timeId", "1",
                    "paymentKey", "test",
                    "orderId", "test",
                    "amount", 1000,
                    "paymentType", "NORMAL"
            );

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .sessionId(sessionId)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value());

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/times/1")
                    .then().log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class SuccessTest {
        @DisplayName("시간 포맷이 유효하여 성공적으로 요청이 된다.")
        @Test
        void reservationTimeAddTest() {
            Map<String, String> request = Map.of("startAt", "12:00");
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/times")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value());
        }
    }
}
