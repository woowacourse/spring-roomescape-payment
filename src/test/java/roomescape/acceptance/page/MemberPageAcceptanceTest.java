package roomescape.acceptance.page;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.acceptance.AcceptanceTest;

import static org.hamcrest.Matchers.is;

class MemberPageAcceptanceTest extends AcceptanceTest {
    @DisplayName("모든 사용자 기본 Page에 접근할 수 있다.")
    @Test
    void responseMemberMainPage() {
        RestAssured.given().log().all()
                .when().get("/")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("로그한 사용자는 예약 페이지에 들어갈 수 있다.")
    @Test
    void responseMemberReservationPage() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .when().get("/reservation")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("로그인하지 않은 사용자는 예약 페이지에 들어갈 수 없다.")
    @Test
    void cannotResponseMemberReservationPage() {
        RestAssured.given().log().all()
                .when().get("/reservation")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("인증되지 않은 사용자입니다."));
    }

    @DisplayName("모든 사용자는 로그인 Page에 접근할 수 있다.")
    @Test
    void responseMemberLoginPage() {
        RestAssured.given().log().all()
                .when().get("/login")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("모든 사용자는 회원가입 Page에 접근할 수 있다.")
    @Test
    void responseMemberSignupPage() {
        RestAssured.given().log().all()
                .when().get("/signup")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("로그인한 사용자는 본인의 예약 Page에 접근할 수 있다.")
    @Test
    void responseMemberOwnReservationPage() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .when().get("/member/reservation")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("로그인하지 않은 사용자는 본인의 예약 Page에 접근할 수 없다.")
    @Test
    void cannotResponseMemberOwnReservationPage() {
        RestAssured.given().log().all()
                .when().get("/member/reservation")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("인증되지 않은 사용자입니다."));
    }
}
