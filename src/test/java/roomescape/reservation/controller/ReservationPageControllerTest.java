package roomescape.reservation.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import roomescape.util.ControllerTest;

@DisplayName("예약 페이지 테스트")
class ReservationPageControllerTest extends ControllerTest {

    @DisplayName("기본 페이지 조회 시, 200을 반환한다.")
    @Test
    void adminMainPage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .when().get("/")
                .then().log().all()
                .apply(document("main/page/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("예약 페이지 조회 시, 200을 반환한다.")
    @Test
    void getAdminReservationPage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when().get("/reservation")
                .then().log().all()
                .apply(document("reservation/page/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("나의 예약 페이지 조회 시, 200을 반환한다.")
    @Test
    void getAdminReservationTimePage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when().get("/reservation-mine")
                .then().log().all()
                .apply(document("my/page/success"))
                .statusCode(HttpStatus.OK.value());
    }
}
