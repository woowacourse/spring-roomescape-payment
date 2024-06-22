package roomescape.member.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static roomescape.fixture.MemberFixture.*;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.auth.service.TokenProvider;
import roomescape.member.service.MemberService;
import roomescape.reservation.service.ReservationRegister;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.reservation.service.ThemeService;
import roomescape.util.ControllerTest;

@DisplayName("관리자 페이지 테스트")
class AdminControllerTest extends ControllerTest {
    @Autowired
    ReservationRegister reservationRegister;

    @Autowired
    ReservationTimeService reservationTimeService;

    @Autowired
    ThemeService themeService;

    @Autowired
    MemberService memberService;

    @Autowired
    TokenProvider tokenProvider;

    String token;

    @BeforeEach
    void beforeEach() {
        token = tokenProvider.createAccessToken(getMemberAdmin().getEmail());
    }

    @DisplayName("관리자 메인 페이지 조회에 성공한다.")
    @Test
    void adminMainPage() {
        //given

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("관리자 접근이 없는 유저에 접근이 제한된다.")
    @ParameterizedTest
    @ValueSource(strings = {"/admin/reservations/", "/admin"})
    void unauthorizedMember(String url) {
        //given
        String unauthorizedMemberToken = tokenProvider.createAccessToken(getMemberTacan().getEmail());

        //when & then
        RestAssured.given().log().all()
                .cookie("token", unauthorizedMemberToken)
                .when().get(url)
                .then().log().all()
                .statusCode(500);
    }

    @DisplayName("관리자 예약 페이지 조회에 성공한다.")
    @Test
    void getAdminReservationPage() {
        //given & when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("예약 대기 페이지에 접근한다.")
    @Test
    void waitingPage () {
        //given

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservation/waiting")
                .then().log().all()
                .statusCode(200);
    }
}
