package roomescape.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import roomescape.application.auth.dto.TokenPayload;
import roomescape.application.member.dto.request.MemberLoginRequest;
import roomescape.application.member.dto.response.MemberResponse;
import roomescape.application.member.dto.response.TokenResponse;
import roomescape.domain.member.Role;

class AuthControllerTest extends ControllerTest {

    @Test
    @DisplayName("사용자가 로그인한다.")
    void loginTest() {
        BDDMockito.given(memberService.login(any(MemberLoginRequest.class)))
                .willReturn(new TokenResponse("authorization-token"));

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

        RestDocumentationResultHandler handler = document(
                "auth-login",
                requestFields(requestFieldDescriptors),
                responseCookies(responseCookieDescriptors)
        );

        givenWithSpec().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .apply(handler)
                .statusCode(204);
    }

    @Test
    @DisplayName("로그인에 실패한다.")
    void loginFailureTest() {
        BDDMockito.willThrow(new IllegalArgumentException("이메일 / 비밀번호를 확인해 주세요."))
                .given(memberService)
                .login(any(MemberLoginRequest.class));

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

        RestDocumentationResultHandler handler = document(
                "auth-login-failure",
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .apply(handler)
                .statusCode(400);
    }

    @Test
    @DisplayName("사용자가 로그아웃한다.")
    void logout() {
        CookieDescriptor[] requestCookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        RestDocumentationResultHandler handler = document(
                "auth-logout",
                requestCookies(requestCookieDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "auth-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .when().post("/logout")
                .then().log().all()
                .apply(handler)
                .statusCode(204)
                .cookie("token", "");
    }

    @Test
    @DisplayName("로그인 상태를 확인한다.")
    void checkLoginStatus() {
        BDDMockito.given(tokenManager.extract(anyString()))
                .willReturn(new TokenPayload(1L, "아루", Role.MEMBER));
        BDDMockito.given(memberService.findById(1L))
                .willReturn(new MemberResponse(1L, "아루"));

        CookieDescriptor[] requestCookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("id").description("ID"),
                fieldWithPath("name").description("이름")
        };

        RestDocumentationResultHandler handler = document(
                "auth-login-check",
                requestCookies(requestCookieDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "auth-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .when().get("/login/check")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }
}
