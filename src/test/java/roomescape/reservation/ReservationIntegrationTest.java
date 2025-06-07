package roomescape.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.common.dto.response.ErrorResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationIntegrationTest {

    @DisplayName("예약 날짜가 없는 상태로 생성 요청 시 400 응답을 준다.")
    @Test
    void when_given_null_date() {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", null);
        reservation.put("themeId", 1L);
        reservation.put("timeId", 1L);
        reservation.put("paymentKey", "paymentKey");
        reservation.put("orderId", "orderId");
        reservation.put("amount", 1000);

        Response response = RestAssured.given().log().all()
                .cookie("token", extractTokenOfAdminLoginMember())
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(actual.timestamp(), BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(), "[ERROR] 예약 날짜는 필수입니다.", "/reservations");

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("잘못된 날짜로 생성 요청 시 400 응답을 준다.")
    @ParameterizedTest
    @ValueSource(strings = {"a", "ab", "123", "2월 5일", "2014년 2월 5일", "2023:12:03", "2024-15-10"})
    void when_given_wrong_date(final String date) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", date);
        reservation.put("timeId", 1);

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(actual.timestamp(), BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(), "[ERROR] 요청 본문 형식이 올바르지 않습니다.", "/reservations");

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("잘못된 예약 시간 번호로 생성 요청 시 400 응답을 준다.")
    @Test
    void when_given_wrong_time_id() {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2024-12-03");
        reservation.put("timeId", "a");

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(actual.timestamp(), BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(), "[ERROR] 요청 본문 형식이 올바르지 않습니다.", "/reservations");

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("예약 시간 번호가 없는 상태로 생성 요청 시 400 응답을 준다.")
    @Test
    void when_given_null_time_id() {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2024-12-03");
        reservation.put("themeId", 1L);
        reservation.put("timeId", null);
        reservation.put("paymentKey", "paymentKey");
        reservation.put("orderId", "orderId");
        reservation.put("amount", 1000);

        Response response = RestAssured.given().log().all()
                .cookie("token", extractTokenOfAdminLoginMember())
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(actual.timestamp(), BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(), "[ERROR] 예약 시간 번호는 필수입니다.", "/reservations");

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("잘못된 예약 id로 삭제 요청 시 400 응답을 준다.")
    @Test
    void when_given_wrong_id() {
        RestAssured.given().log().all()
                .when().delete("/reservations/10")
                .then().log().all()
                .statusCode(BAD_REQUEST.value());
    }

    private String extractTokenOfAdminLoginMember() {
        final Map<String, String> loginParams = new HashMap<>();
        loginParams.put("email", "member1@email.com");
        loginParams.put("password", "password");

        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams)
                .when().post("/login")
                .then().log().all()
                .statusCode(OK.value())
                .extract();

        String token = response.cookie("token");
        if (token == null) {
            throw new IllegalStateException("로그인 응답에서 토큰 쿠키를 찾을 수 없습니다.");
        }

        return token;
    }
}
