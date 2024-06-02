package roomescape.member.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import roomescape.util.ControllerTest;

@DisplayName("관리자 페이지 테스트")
class AdminPageControllerTest extends ControllerTest {

    @DisplayName("관리자 메인 페이지 조회 시, 200을 반환한다.")
    @Test
    void adminMainPage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/admin")
                .then().log().all()
                .apply(document("admin-main/page/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자 예약 페이지 조회 시, 200을 반환한다.")
    @Test
    void getAdminReservationPage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .apply(document("admin-reservation/page/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자 시간 관리 페이지 조회 시, 200을 반환한다.")
    @Test
    void getAdminReservationTimePage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/admin/time")
                .then().log().all()
                .apply(document("admin-time/page/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자 테마 페이지 조회 시, 200을 반환한다.")
    @Test
    void getAdminThemePage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/admin/theme")
                .then().log().all()
                .apply(document("admin-theme/page/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자 예약 대기 페이지 조회 시, 200을 반환한다.")
    @Test
    void getAdminWaitingPage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/admin/waiting")
                .then().log().all()
                .apply(document("admin-waiting/page/success"))
                .statusCode(HttpStatus.OK.value());
    }
}
