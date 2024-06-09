package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import roomescape.RestClientControllerTest;
import roomescape.auth.token.TokenProvider;
import roomescape.fixture.PaymentConfirmFixtures;
import roomescape.member.model.MemberRole;
import roomescape.payment.infrastructure.PaymentGateway;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationTimeResponse;
import roomescape.reservation.dto.SaveAdminReservationRequest;
import roomescape.reservation.dto.SaveReservationRequest;
import roomescape.reservation.dto.SaveReservationTimeRequest;
import roomescape.reservation.dto.SaveThemeRequest;
import roomescape.reservation.dto.SearchReservationsRequest;
import roomescape.reservation.dto.ThemeResponse;

class AdminReservationControllerTest extends RestClientControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @MockBean
    private PaymentGateway paymentGateway;

    @DisplayName("전체 예약 정보를 조회한다.")
    @Test
    void getReservationsTest() {
        RestAssured.given(spec).log().all()
                .filter(document("admin-findAll-reservations"))
                .cookie("token", createAdminAccessToken())
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(16));
    }

    @DisplayName("회원 아이디, 테마 아이디, 날짜 범위를 입력받아 예약정보를 조회한다.")
    @Test
    void searchReservations() {
        final SearchReservationsRequest request =
                new SearchReservationsRequest(1L, 1L, LocalDate.now().minusDays(4), LocalDate.now());
        RestAssured.given(spec).log().all()
                .filter(document("admin-search-reservations"))
                .cookie("token", createAdminAccessToken())
                .queryParam("memberId", request.memberId())
                .queryParam("themeId", request.themeId())
                .queryParam("from", request.from().toString())
                .queryParam("to", request.to().toString())
                .when().get("/admin/reservations/search")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }


    @DisplayName("(관리자) - 사용자 아이디를 포함하여 예약 정보를 저장한다.")
    @Sql("classpath:test-payment-credential-data.sql")
    @Test
    void saveReservationForAdminTest() {
        final SaveAdminReservationRequest saveReservationRequest = new SaveAdminReservationRequest(
                LocalDate.now().plusDays(1),
                3L,
                1L,
                1L
        );
        given(paymentGateway.confirm(anyString(), anyLong(), anyString()))
                .willReturn(PaymentConfirmFixtures.getDefaultResponse("orderId", "1234", 1000L));

        RestAssured.given(spec).log().all()
                .filter(document("admin-save-reservation"))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationRequest)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", is(17));
    }

    @DisplayName("관리자가 아닌 클라이언트가 회원 아이디를 포함하여 예약 정보를 저장하려고 하면 에러 코드가 응답된다.")
    @Test
    void saveReservationIncludeMemberIdWhoNotAdminTest() {
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(1),
                1L,
                1L,
                "orderId",
                1000L,
                "paymentKey"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveReservationRequest)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("예약 정보를 삭제한다.")
    @Test
    void deleteReservationTest() {
        RestAssured.given(spec).log().all()
                .filter(document("admin-delete-reservation"))
                .cookie("token", createAdminAccessToken())
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(204);

        final List<ReservationResponse> reservations = RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ReservationResponse.class);

        assertThat(reservations).hasSize(15);
    }

    @DisplayName("관리자가 아닌 클라이언트가 예약 정보를 삭제하려고 하면 에러 코드가 응답된다.")
    @Test
    void deleteReservationWhoNotAdminTest() {
        RestAssured.given().log().all()
                .cookie("token", createUserAccessToken())
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("예약 시간 정보를 저장한다.")
    @Test
    void saveReservationTimeTest() {
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(
                LocalTime.of(12, 15));

        RestAssured.given(spec).log().all()
                .filter(document("admin-save-reservation-time"))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationTimeRequest)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201)
                .body("id", is(9));
    }

    @DisplayName("관리자가 아닌 클라이언트가 예약 시간 정보를 저장하려고 하면 예외를 발생시킨다.")
    @Test
    void saveReservationTimeWhoNotAdminTest() {
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(
                LocalTime.of(12, 15));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveReservationTimeRequest)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("예약 시간 정보를 삭제한다.")
    @Test
    void deleteReservationTimeTest() {
        // 예약 시간 정보 삭제
        RestAssured.given(spec).log().all()
                .filter(document("admin-delete-reservation-time"))
                .cookie("token", createAdminAccessToken())
                .when().delete("/admin/times/2")
                .then().log().all()
                .statusCode(204);

        // 예약 시간 정보 조회
        final List<ReservationTimeResponse> reservationTimes = RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/times")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ReservationTimeResponse.class);

        assertThat(reservationTimes).hasSize(7);
    }

    @DisplayName("관리자가 아닌 클라이언트가 예약 시간 정보를 삭제하려고 하면 예외를 발생시킨다.")
    @Test
    void deleteReservationTimeWhoNotAdminTest() {
        RestAssured.given().log().all()
                .cookie("token", createUserAccessToken())
                .when().delete("/admin/times/2")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("테마 정보를 저장한다.")
    @Test
    void saveThemeTest() {
        final SaveThemeRequest saveThemeRequest = new SaveThemeRequest(
                "즐거운 방방탈출~",
                "방방방! 탈탈탈!",
                "방방 사진"
        );

        RestAssured.given(spec).log().all()
                .filter(document("admin-save-theme"))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveThemeRequest)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201)
                .body("id", is(16));
    }

    @DisplayName("관리자가 아닌 클라이언트가 테마 정보를 저장하려고 하면 예외를 발생시킨다.")
    @Test
    void saveThemeWhoNotAdminTest() {
        final SaveThemeRequest saveThemeRequest = new SaveThemeRequest(
                "즐거운 방방탈출~",
                "방방방! 탈탈탈!",
                "방방 사진"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveThemeRequest)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("테마 정보를 삭제한다.")
    @Test
    void deleteThemeTest() {
        // 예약 시간 정보 삭제
        RestAssured.given(spec).log().all()
                .filter(document("admin-delete-theme"))
                .cookie("token", createAdminAccessToken())
                .when().delete("/admin/themes/7")
                .then().log().all()
                .statusCode(204);

        // 예약 시간 정보 조회
        final List<ThemeResponse> themes = RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/themes")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ThemeResponse.class);

        assertThat(themes).hasSize(14);
    }

    @DisplayName("관리자가 아닌 클라이언트가 테마 정보를 삭제하려고 하면 예외를 발생시킨다.")
    @Test
    void deleteThemeWhoNotAdminTest() {
        // 예약 시간 정보 삭제
        RestAssured.given().log().all()
                .cookie("token", createUserAccessToken())
                .when().delete("/admin/themes/7")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    private String createUserAccessToken() {
        return tokenProvider.createToken(3L, MemberRole.USER);
    }

    private String createAdminAccessToken() {
        return tokenProvider.createToken(1L, MemberRole.ADMIN);
    }
}
