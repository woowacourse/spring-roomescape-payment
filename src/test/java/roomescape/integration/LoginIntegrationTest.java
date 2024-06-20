package roomescape.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import roomescape.domain.member.Member;
import roomescape.service.login.dto.LoginCheckResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

class LoginIntegrationTest extends IntegrationTest {
    @Nested
    @DisplayName("로그인 API")
    class Login {
        Map<String, String> params = new HashMap<>();
        Member member;

        @BeforeEach
        void setUp() {
            member = memberFixture.createUserMember();
        }

        @Test
        void 이메일과_비밀번호로_로그인할_수_있다() {
            params.put("email", member.getEmail().address());
            params.put("password", member.getPassword().password());

            RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(params)
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(200);
        }

        @Test
        void 이메일이나_비밀번호가_틀리면_로그인할_수_없다() {
            params.put("email", member.getEmail().address());
            params.put("password", "wrongpassword");

            RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(params)
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(401);
        }
    }

    @Nested
    @DisplayName("인증 정보 조회 API")
    class LoginCheck {
        @BeforeEach
        void setUp() {
            memberFixture.createUserMember();
        }

        @Test
        void 쿠키에_토큰을_담아_로그인한_사용자_정보를_조회할_수_있다() {
            LoginCheckResponse response = RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .when().get("/login/check")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value()).extract().as(LoginCheckResponse.class);

            assertThat(response.getName()).isEqualTo("사용자");
        }

        @Test
        void 쿠키에_토큰이_존재하지_않으면_예외가_발생한다() {
            RestAssured.given().log().all()
                    .when().get("/login/check")
                    .then().log().all()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        void 쿠키의_토큰이_유효하지_않으면_예외가_발생한다() {
            RestAssured.given().log().all()
                    .header("Cookie", "token=asdfadsfcx.safsdf.scdsafd")
                    .when().get("/login/check")
                    .then().log().all()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Nested
    @DisplayName("로그아웃 API")
    class Logout {
        @Test
        void 쿠키에_토큰을_담아_로그아웃_할_수_있다() {
            memberFixture.createUserMember();

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .when().post("/logout")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    @DisplayName("회원가입 API")
    class Signup {
        @Test
        void 일반유저_권한으로_회원가입을_할_수_있다() {
            Map<String, String> params = new HashMap<>();
            params.put("email", "user@gmail.com");
            params.put("password", "1234567890");
            params.put("name", "사용자");

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/signup")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/members/1")
                    .body("id", is(1))
                    .body("role", is("USER"));
        }
    }
}
