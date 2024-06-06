package roomescape.acceptance;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("사용자가 로그인한다.")
    void loginTest() {
        fixture.registerMember(MEMBER_ARU.registerRequest());

        String request = """
                    {
                        "email": "member@test.com",
                        "password": "12341234"
                    }
                """;

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("비밀번호")
        };

        CookieDescriptor[] responseCookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        RestDocumentationFilter documentFilter = document(
                "auth-login",
                requestFields(requestFieldDescriptors),
                responseCookies(responseCookieDescriptors)
        );

        givenWithSpec().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .filter(documentFilter)
                .when().post("/login")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("로그인에 실패한다.")
    void loginFailureTest() {
        String request = """
                    {
                        "email": "member@test.com",
                        "password": "12341234"
                    }
                """;

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("비밀번호")
        };

        RestDocumentationFilter documentFilter = document(
                "auth-login-failure",
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .filter(documentFilter)
                .when().post("/login")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("사용자가 로그아웃한다.")
    void logout() {
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());

        CookieDescriptor[] requestCookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        RestDocumentationFilter documentFilter = document(
                "auth-logout",
                requestCookies(requestCookieDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", token)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(documentFilter)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204)
                .cookie("token", "");
    }

    @Test
    @DisplayName("로그인 상태를 확인한다.")
    void checkLoginStatus() {
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());

        CookieDescriptor[] requestCookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("id").description("ID"),
                fieldWithPath("name").description("이름")
        };

        RestDocumentationFilter documentFilter = document(
                "auth-login-check",
                requestCookies(requestCookieDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", token)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(documentFilter)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200);
    }
}
