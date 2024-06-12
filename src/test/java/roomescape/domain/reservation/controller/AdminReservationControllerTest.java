package roomescape.domain.reservation.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.auth.token.TokenProvider;
import roomescape.config.TestConfig;
import roomescape.domain.member.model.MemberRole;
import roomescape.domain.reservation.dto.ReservationResponse;
import roomescape.domain.reservation.dto.ReservationTimeResponse;
import roomescape.domain.reservation.dto.SaveAdminReservationRequest;
import roomescape.domain.reservation.dto.SaveReservationRequest;
import roomescape.domain.reservation.dto.SaveReservationTimeRequest;
import roomescape.domain.reservation.dto.SaveThemeRequest;
import roomescape.domain.reservation.dto.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@Import(TestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdminReservationControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @LocalServerPort
    int randomServerPort;

    @BeforeEach
    public void initReservation() {
        RestAssured.port = randomServerPort;
    }

    @DisplayName("전체 예약 정보를 조회한다.")
    @Test
    void getReservationsTest() {
        RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(16));
    }


    @DisplayName("(관리자) - 사용자 아이디를 포함하여 예약 정보를 저장한다.")
    @Test
    void saveReservationForAdminTest() {
        final SaveAdminReservationRequest saveReservationRequest = new SaveAdminReservationRequest(
                LocalDate.now().plusDays(1),
                3L,
                1L,
                1L,
                "test-order-id",
                "test_order_name",
                10000L,
                "test_payment_key"
        );

        RestAssured.given().log().all()
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
                null,
                1L,
                1L,
                "test_order_id",
                10000L,
                "test_payment_key"
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
        RestAssured.given().log().all()
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

        final boolean isCancel = reservations.stream()
                .anyMatch(reservationResponse -> reservationResponse.status().equals("취소"));

        assertThat(isCancel).isTrue();
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
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(LocalTime.of(12, 15));

        RestAssured.given().log().all()
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
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(LocalTime.of(12, 15));

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
        RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().delete("/admin/times/2")
                .then().log().all()
                .statusCode(204);

        // 예약 시간 정보 조회
        final List<ReservationTimeResponse> reservationTimes = RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/admin/times")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ReservationTimeResponse.class);

        assertThat(reservationTimes.size()).isEqualTo(7);
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

        RestAssured.given().log().all()
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
        RestAssured.given().log().all()
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

        assertThat(themes.size()).isEqualTo(14);
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
