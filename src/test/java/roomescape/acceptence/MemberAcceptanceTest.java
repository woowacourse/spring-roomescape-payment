package roomescape.acceptence;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class MemberAcceptanceTest extends AcceptanceFixture {

    @Test
    @DisplayName("회원을 저장한다.")
    void save_ShouldSaveMember() {
        RestDocumentationFilter filter = document("member/save",
                requestFields(
                        fieldWithPath("name").description("회원 이름"),
                        fieldWithPath("email").description("회원 이메일, 중복X"),
                        fieldWithPath("password").description("회원 비밀번호")
                )
        );

        // given
        Map<String, String> requestBody = Map.of("name", "aa", "email", "aa@aa.aa", "password", "aa");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .contentType(ContentType.JSON)
                .body(requestBody)

                .when()
                .post("/members")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/members/1");
    }

    @Test
    @DisplayName("로그인")
    void login_ShouldSignIn() {
        RestDocumentationFilter filter = document("login",
                requestFields(
                        fieldWithPath("email").description("회원 이메일"),
                        fieldWithPath("password").description("회원 비밀번호")
                )
        );

        // given
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "aa", "email", "aa@aa.aa", "password", "aa"))

                .when()
                .post("/members")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/members/1");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .contentType(ContentType.JSON)
                .body(Map.of("email", "aa@aa.aa", "password", "aa"))

                .when()
                .post("/login")

                .then().log().all()
                .statusCode(is(HttpStatus.SC_OK))
                .header(HttpHeaders.SET_COOKIE, anything());
    }

    @Test
    @DisplayName("모든 회원 정보를 조회한다.")
    void findAll_ShouldInquiryAllMembers() {
        RestDocumentationFilter filter = document("member/search",
                requestCookies(
                        cookieWithName("token").description("일반 사용자 권한 토콘")
                ),
                responseFields(
                        fieldWithPath("[].id").description("회원 식별자"),
                        fieldWithPath("[].name").description("회원 이름")
                )

        );
        // given
        Map<String, String> requestBody1 = Map.of("name", "aa", "email", "aa@aa.aa", "password", "aa");
        Map<String, String> requestBody2 = Map.of("name", "bb", "email", "bb@bb.bb", "password", "bb");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody1)

                .when()
                .post("/members")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/members/1");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody2)

                .when()
                .post("/members")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/members/2");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .cookie(normalToken)
                .accept(ContentType.JSON)

                .when()
                .get("/members")

                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].name", is("aa"))
                .body("[1].id", is(2))
                .body("[1].name", is("bb"));
    }

    @Test
    @DisplayName("로그인 상태 확인")
    void loginCheck_ShouldCheckLoginStatus() {
        RestDocumentationFilter filter = document("login/check",
                requestCookies(
                        cookieWithName("token").description("일반 사용자 권한 토콘")
                ),
                responseFields(
                        fieldWithPath("id").description("회원 식별자"),
                        fieldWithPath("name").description("회원 이름")
                )
        );

        // given
        // 회원가입
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "aa", "email", "aa@aa.aa", "password", "aa"))

                .when()
                .post("/members")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/members/1");

        // 로그인
        String token = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "aa@aa.aa", "password", "aa"))

                .when()
                .post("/login")

                .thenReturn()
                .cookie("token");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .cookie("token", token)
                .accept(ContentType.JSON)

                .when()
                .get("/login/check")

                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("id", is(1))
                .body("name", is("aa"));
    }
}
