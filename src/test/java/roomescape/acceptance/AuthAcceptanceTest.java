package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.service.auth.dto.LoginRequest;
import roomescape.service.auth.dto.SignUpRequest;

import static org.hamcrest.Matchers.is;

class AuthAcceptanceTest extends AcceptanceTest {
    @DisplayName("사용자가 성공적으로 로그인한다.")
    @Test
    void login() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("guest123", "guest@email.com"))
                .when().post("/login")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("사용자가 로그아웃을 한다.")
    @Test
    void logout() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .contentType(ContentType.JSON)
                .when().post("/logout")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("인증 정보를 조회한다.")
    @Test
    void check() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .when().get("/login/check")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value()).body("name", is("guest"));
    }

    @DisplayName("사용자가 회원가입을 한다.")
    @Test
    void signUp() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new SignUpRequest("lini", "lini@email.com", "lini123"))
                .when().post("/signup")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("이미 회원가입된 이메일로 회원가입할 수 없다.")
    @Test
    void cannotSignUp() {
        //given
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new SignUpRequest("lini", "lini@email.com", "lini123"))
                .when().post("/signup")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value());

        //when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new SignUpRequest("lini2", "lini@email.com", "lini1234"))
                .when().post("/signup")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
