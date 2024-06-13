package roomescape.auth.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.auth.dto.LoginRequest;
import roomescape.common.config.IntegrationTest;
import roomescape.common.util.CookieUtils;
import roomescape.member.dto.MemberSignUpRequest;

class AuthControllerTest extends IntegrationTest {

    @DisplayName("회원 가입 후 로그인에 성공하면 200 응답을 받고 쿠키가 존재하는지 확인하고 로그인 체크 한다.")
    @Test
    void loginAndCheck() {
        String name = "카키";
        String email = "kaki@email.com";
        String password = "1234";
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(name, email, password);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(memberSignUpRequest)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);

        LoginRequest loginRequest = new LoginRequest(email, password);
        Response response = RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .filter(document("/auth/login"))
                .when().post("/login")
                .thenReturn();

        String cookie = String.valueOf(response.getDetailedCookie(CookieUtils.TOKEN_KEY));
        RestAssured.given(this.spec).log().all()
                .cookie(cookie)
                .filter(document("/auth/findMemberName"))
                .when().get("/login/member")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("이메일이 일치하지 않으면 로그인에 실패한다.")
    @Test
    void failLoginWhenInvalidEmail() {
        saveMemberAsAnna();

        LoginRequest loginRequest = new LoginRequest("annna@email.com", "1234");
        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .filter(document("/auth/login/fail/invalid-email"))
                .when().post("/login")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다.")
    @Test
    void failLoginWhenInvalidPassword() {
        saveMemberAsAnna();

        LoginRequest loginRequest = new LoginRequest("anna@email.com", "123456");
        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .filter(document("/auth/login/fail/invalid-password"))
                .when().post("/login")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("로그아웃을 하면 해당 사용자의 쿠키를 제거한다.")
    @Test
    void logout() {
        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, "cookieValue")
                .filter(document("/auth/logout"))
                .when()
                .post("/logout")
                .then().log().all()
                .statusCode(200)
                .header("Set-Cookie", containsString("token=;"))
                .header("Set-Cookie", containsString("Max-Age=0"))
                .header("Set-Cookie", containsString("Path=/"));
    }
}
