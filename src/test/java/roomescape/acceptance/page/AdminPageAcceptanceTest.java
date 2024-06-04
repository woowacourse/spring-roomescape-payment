package roomescape.acceptance.page;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.acceptance.AcceptanceTest;

import static org.hamcrest.Matchers.is;

class AdminPageAcceptanceTest extends AcceptanceTest {
    @DisplayName("관리자는 Admin Page 홈화면에 접근할 수 있다.")
    @Test
    void responseAdminPage() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("일반 사용자는 Admin Page 홈화면에 접근할 수 없다.")
    @Test
    void cannotResponseAdminPageIfNotAdmin() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .when().get("/admin")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
    }

    @DisplayName("로그인하지 않은 사용자는 Admin Page 홈화면에 접근할 수 없다.")
    @Test
    void cannotResponseAdminPageIfNotMember() {
        RestAssured.given().log().all()
                .when().get("/admin")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("인증되지 않은 사용자입니다."));
    }

    @DisplayName("관리자는 Admin Reservation Page에 접근할 수 있다.")
    @Test
    void responseAdminReservationPage() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자는 Reservation Time Page에 접근할 수 있다.")
    @Test
    void responseReservationTimePage() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin/time")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자는 Admin Theme Page에 접근할 수 있다.")
    @Test
    void responseThemePage() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin/theme")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }
}
