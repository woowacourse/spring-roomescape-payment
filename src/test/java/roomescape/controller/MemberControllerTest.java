package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import roomescape.controller.config.ControllerTestSupport;

import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

class MemberControllerTest extends ControllerTestSupport {

    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "1234";
    private static final String TEST_NAME = "테스트";

    String createdId;
    int memberSize;

    @Test
    @DisplayName("회원 목록 조회")
    void showMember() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 배열"),
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("[].name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("[].role").type(JsonFieldType.STRING).description("역할"));
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, responseFields))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().get("/admin/members")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("회원 추가")
    void saveMember() {
        RequestFieldsSnippet requestFields = requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"));
        Map<String, String> params = Map.of(
                "email", "ever@email.com",
                "password", "password",
                "name", "ever");
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteMember() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));
        Map<String, String> params = Map.of(
                "email", "ever2@email.com",
                "password", "password",
                "name", "ever2");
        String createdId = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .statusCode(201).extract().header("location").split("/")[2];

        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/admin/members/" + createdId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("회원 CRUD")
    @TestFactory
    Stream<DynamicTest> dynamicUserTestsFromCollection() {
        return Stream.of(
                dynamicTest("회원 목록을 조회한다.", () -> {
                    memberSize = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/admin/members")
                            .then().log().all()
                            .statusCode(200).extract()
                            .response().jsonPath().getList("$").size();
                }),
                dynamicTest("이메일 형식이 아니면 회원가입할 수 없다.", () -> {
                    Map<String, String> params = Map.of(
                            "email", "asdfdasf",
                            "password", TEST_PASSWORD,
                            "name", TEST_NAME
                    );

                    RestAssured
                            .given().log().all()
                            .body(params)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when().post("/members")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("회원가입", () -> {
                    Map<String, String> params = Map.of(
                            "email", TEST_EMAIL,
                            "password", TEST_PASSWORD,
                            "name", TEST_NAME
                    );

                    createdId = RestAssured
                            .given().log().all()
                            .body(params)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when().post("/members")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[2];
                }),
                dynamicTest("회원 목록 개수가 1증가한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/admin/members")
                            .then().log().all()
                            .statusCode(200).body("size()", is(memberSize + 1));
                }),
                dynamicTest("중복된 이메일로 회원가입할 수 없다.", () -> {
                    Map<String, String> params = Map.of(
                            "email", TEST_EMAIL,
                            "password", TEST_PASSWORD,
                            "name", TEST_NAME
                    );

                    RestAssured
                            .given().log().all()
                            .body(params)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when().post("/members")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("회원을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/members/" + createdId)
                            .then().log().all()
                            .statusCode(204);
                }),
                dynamicTest("회원 목록 개수가 1감소한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/admin/members")
                            .then().log().all()
                            .statusCode(200).body("size()", is(memberSize));
                })
        );
    }
}
