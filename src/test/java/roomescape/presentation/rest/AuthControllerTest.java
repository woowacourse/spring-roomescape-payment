package roomescape.presentation.rest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;


class AuthControllerTest {

    static String getToken(String email, String password) {
        return RestAssured.given().contentType(ContentType.JSON).body(Map.of("email", email, "password", password))
                .when().post("/login")
                .then().statusCode(HttpStatus.OK.value()).extract().response().getDetailedCookies()
                .getValue("token");
    }

    @Nested
    @DisplayName("로그인 한다.")
    class Login extends RestDocsTestBase {

        @Test
        @DisplayName("잘못된 비밀 번호로 로그인 하는 경우 예외를 던진다")
        void adminLogin_WhenPasswordIsWrong() {
            RestAssured.given(spec).filter(adminLogin_WhenPasswordIsWrong_Document())
                    .contentType(ContentType.JSON).body(Map.of(
                            "email",
                            "admin@email.com",
                            "password",
                            "wrong"
                    ))
                    .when().post("/login")
                    .then().statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("잘못된 이메일로 로그인 하는 경우 예외를 던진다")
        void adminLogin_WhenEmailNotExist() {
            RestAssured.given(spec).filter(adminLogin_WhenEmailNotExist_Document()).contentType(ContentType.JSON)
                    .body(Map.of("email", "wrong@email.com", "password", "password"))
                    .when().post("/login")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("정상적으로 로그인한다")
        void login() {
            RestAssured.given(spec).log().all()
                    .filter(login_Document()).contentType(ContentType.JSON)
                    .body(Map.of("email", "user1@email.com", "password", "password1"))
                    .when().post("/login")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value());
        }

        RestDocumentationFilter adminLogin_WhenPasswordIsWrong_Document() {
            FieldDescriptor[] requestFields = {
                    fieldWithPath("email").description("로그인 이메일(ID)"),
                    fieldWithPath("password").description("로그인 비밀번호(PW)"),
            };

            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            return document(
                    "login-wrong-password",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    responseFields(responseFields)
            );
        }

        RestDocumentationFilter adminLogin_WhenEmailNotExist_Document() {
            FieldDescriptor[] requestFields = {
                    fieldWithPath("email").description("로그인 이메일(ID)"),
                    fieldWithPath("password").description("로그인 비밀번호(PW)"),
            };

            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            return document(
                    "login-wrong-email",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    responseFields(responseFields)
            );
        }

        RestDocumentationFilter login_Document() {
            FieldDescriptor[] requestFields = {
                    fieldWithPath("email").description("로그인 이메일(ID)"),
                    fieldWithPath("password").description("로그인 비밀번호(PW)"),
            };

            CookieDescriptor[] responseCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    responseCookies(responseCookies)
            );
        }
    }

    @Nested
    @DisplayName("로그인된 사용자의 정보를 조회한다.")
    class LoginCheck extends RestDocsTestBase {

        @Test
        @DisplayName("어드민 계정으로 로그인 한다")
        void adminLogin() {
            var token = getToken("admin@email.com", "password");

            RestAssured.given().contentType(ContentType.JSON).cookie("token", token)
                    .when().get("/login/check")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("사용자 계정으로 로그인 한다")
        void userLogin() {
            final var token = getToken("user1@email.com", "password1");

            RestAssured.given(spec).filter(userLogin_Document()).contentType(ContentType.JSON).cookie("token", token)
                    .when().get("/login/check")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        RestDocumentationFilter userLogin_Document() {
            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("id").description("사용자 ID"),
                    fieldWithPath("name").description("사용자 이름")
            };

            return document(
                    "login-check",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    responseFields(responseFields)
            );
        }
    }


    @Nested
    @DisplayName("로그아웃 한다.")
    class Logout extends RestDocsTestBase {

        @Test
        @DisplayName("로그아웃 시 토큰 쿠키가 삭제되고 메인 페이지로 리다이렉트 된다")
        void logout() {
            var token = getToken("user1@email.com", "password1");

            RestAssured.given(spec).filter(logout_Document()).cookie("token", token).redirects()
                    .follow(false) // 리다이렉트 따라가지 않게 설정
                    .when().post("/logout")
                    .then().statusCode(HttpStatus.FOUND.value()).cookie("token", "");
        }

        RestDocumentationFilter logout_Document() {

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰").optional()
            };

            CookieDescriptor[] responseCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "logout",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    responseCookies(responseCookies)
            );
        }
    }
}
