package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.controller.request.MemberLoginRequest;
import roomescape.controller.request.RegisterRequest;
import roomescape.model.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.key;

class MemberControllerTest extends AbstractControllerTest {

    @DisplayName("로그인 요청시 jwt 토큰을 얻는다.")
    @Test
    void should_response_cookie_when_login() {
        String email = "sun@email.com";
        String password = "1234";
        String jwtToken = getAuthenticationCookie(email, password);
        assertSoftly(assertSoftly -> {
            assertSoftly.assertThat(jwtToken).isNotBlank();
        });
    }

    @DisplayName("요청시 쿠키를 제공하면 이름을 반환한다.")
    @Test
    void should_response_member_name_when_given_cookie() {
        String email = "sun@email.com";
        String password = "1234";

        RestDocumentationFilter description = document("login-check-success-get",
                requiredCookie,
                responseFields(fieldWithPath("name").description("로그인 한 회원의 이름을 반환합니다.")
                        .attributes(key("title").value("asdf")).type(JsonFieldType.STRING))
        );
        String name = RestAssured
                .given(spec).log().all()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .filter(description)
                .cookie("token", getAuthenticationCookie(email, password))
                .when().get("/login/check")
                .then().statusCode(200)
                .extract().jsonPath().get("name");

        assertThat(name).isEqualTo("썬");
    }

    @DisplayName("모든 사용자들을 반환한다.")
    @Test
    void should_response_all_members() {
        RestDocumentationFilter description = document("members-success-get",
                responseFields(
                        fieldWithPath("[].id").description("회원 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].name").description("회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].role").description("회원 권한").type(JsonFieldType.STRING),
                        fieldWithPath("[].email").description("회원 이메일").type(JsonFieldType.STRING),
                        fieldWithPath("[].password").description("회원 비밀번호").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .contentType(ContentType.JSON)
                .when().get("/members")
                .then().statusCode(200).log().all()
                .extract().jsonPath().getList(".", Member.class);
    }

    @DisplayName("사용자를 등록한다.")
    @Test
    void should_add_member() {
        RestDocumentationFilter description = document("members-success-post",
                requestFields(
                        fieldWithPath("name").description("등록할 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("email").description("등록할 회원 이메일").type(JsonFieldType.STRING),
                        fieldWithPath("password").description("등록할 회원 비밀번호").type(JsonFieldType.STRING)
                ),
                responseFields(
                        fieldWithPath("id").description("가입한 회원의 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("name").description("가입한 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("role").description("가입한 회원 권한").type(JsonFieldType.STRING),
                        fieldWithPath("email").description("가입한 회원 이메일").type(JsonFieldType.STRING),
                        fieldWithPath("password").description("가입한 회원 비밀번호").type(JsonFieldType.STRING)
                )
        );
        RegisterRequest request = new RegisterRequest("포케", "poke@email.com", "1234");
        RestAssured.given(spec).log().all()
                .filter(description)
                .body(request)
                .contentType(ContentType.JSON)
                .when().post("/members")
                .then().statusCode(201).log().all();
    }

    @DisplayName("로그인에 성공하면 인증에 필요한 토큰을 반환한다.")
    @Test
    void should_login_response_response_token() {
        String email = "sun@email.com";
        String password = "1234";

        RestDocumentationFilter description = document("login-success-post",
                requestFields(
                        fieldWithPath("email").description("등록된 회원 이메일 주소").type(JsonFieldType.STRING),
                        fieldWithPath("password").description("회원 이메일에 대한 패스워드").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                        headerWithName("Set-Cookie").description("로그인 한 회원의 인증 토큰")
                )
        );
        MemberLoginRequest request = new MemberLoginRequest("1234", "sun@email.com");
        RestAssured
                .given(spec).log().all()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .filter(description)
                .body(request)
                .when().post("/login")
                .then().statusCode(200);
    }

    @DisplayName("로그아웃을 하면 인증 토큰을 삭제합니다.")
    @Test
    void should_logout_response_removed_token() {
        RestDocumentationFilter description = document("logout-success-post",
                requiredCookie
        );
        RestAssured
                .given(spec).log().all()
                .accept(ContentType.JSON)
                .cookie("token", getMemberCookie())
                .contentType(ContentType.JSON)
                .filter(description)
                .when().post("/logout")
                .then().statusCode(200);
    }
}
