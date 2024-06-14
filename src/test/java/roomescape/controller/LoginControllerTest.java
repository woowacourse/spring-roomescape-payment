package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.restdocs.cookies.ResponseCookiesSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import roomescape.controller.config.ControllerTestSupport;
import roomescape.controller.dto.LoginRequest;
import roomescape.service.dto.MemberResponse;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

class LoginControllerTest extends ControllerTestSupport {

    String accessToken;

    @Test
    @DisplayName("로그인")
    void login() {
        RequestFieldsSnippet requestFields = requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"));
        ResponseCookiesSnippet responseCookies = responseCookies(
                cookieWithName("token").description("회원 인증 토큰"));
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestFields, responseCookies))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))
                .when().post("/login")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰"));
        String cookie = RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies))
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().post("/logout")
                .then().log().all()
                .statusCode(200).extract().cookie("token");

        assertThat(cookie).isEmpty();
    }

    @Test
    @DisplayName("로그인 확인")
    void loginCheck() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("role").type(JsonFieldType.STRING).description("역할"));
        MemberResponse member = RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, responseFields))
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200).extract().as(MemberResponse.class);

        assertThat(member.name()).isEqualTo(ADMIN_NAME);
    }

    @DisplayName("토큰으로 로그인 인증한다.")
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromCollection() {
        return Stream.of(
                dynamicTest("이메일, 패스워드로 로그인한다.", () -> {
                    accessToken = RestAssured
                            .given().log().all()
                            .body(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .when().post("/login")
                            .then().log().all().extract().cookie("token");
                }),
                dynamicTest("토큰으로 로그인 여부를 확인하여 이름을 받는다.", () -> {
                    MemberResponse member = RestAssured
                            .given().log().all()
                            .cookie("token", accessToken)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .when().get("/login/check")
                            .then().log().all()
                            .statusCode(HttpStatus.OK.value()).extract().as(MemberResponse.class);

                    assertThat(member.name()).isEqualTo(ADMIN_NAME);
                }),
                dynamicTest("로그아웃하면 토큰이 비어있다.", () -> {
                    String cookie = RestAssured
                            .given().log().all()
                            .cookie("token", accessToken)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .when().post("/logout")
                            .then().log().all()
                            .statusCode(HttpStatus.OK.value()).extract().cookie("token");

                    assertThat(cookie).isEmpty();
                })
        );
    }
}
