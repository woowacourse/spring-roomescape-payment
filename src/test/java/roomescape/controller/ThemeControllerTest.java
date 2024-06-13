package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;
import roomescape.controller.config.ControllerTestSupport;

import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

class ThemeControllerTest extends ControllerTestSupport {

    String createdId;
    int themeSize;

    @Test
    @DisplayName("테마 목록 조회")
    void showTheme() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 배열"),
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("[].name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("[].description").type(JsonFieldType.STRING).description("설명"),
                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("썸네일"));

        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, responseFields))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().get("/themes")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("테마 추가")
    void saveTheme() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰"));
        RequestFieldsSnippet requestFields = requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("썸네일"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("썸네일"));

        Map<String, String> param = Map.of(
                "name", "테마_테스트",
                "description", "설명_테스트",
                "thumbnail", "썸네일");
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, requestFields, responseFields))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .body(param)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("테마 삭제")
    void deleteTheme() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));

        Map<String, String> param = Map.of(
                "name", "테마_테스트",
                "description", "설명_테스트",
                "thumbnail", "썸네일");
        String createdId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .body(param)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201).extract().header("location").split("/")[2];

        RestAssured.given(specification).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(makeDocumentFilter(requestCookies))
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/admin/themes/" + createdId)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("인기 테마 조회")
    void showPopularTheme() {
        QueryParametersSnippet request = queryParameters(
                parameterWithName("startDate").description("시작 날짜"),
                parameterWithName("endDate").description("끝 날짜"),
                parameterWithName("limit").description("개수"));
        ResponseFieldsSnippet response = responseFields(
                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 배열"),
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("[].name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("[].description").type(JsonFieldType.STRING).description("설명"),
                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("썸네일"));

        Map<String, String> params = Map.of(
                "startDate", "2000-01-01",
                "endDate", "9999-09-09",
                "limit", "2");
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(request, response))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParams(params)
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("테마 생성 조회")
    @TestFactory
    Stream<DynamicTest> dynamicUserTestsFromCollection() {
        return Stream.of(
                dynamicTest("테마 목록을 조회한다.", () -> {
                    themeSize = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/themes")
                            .then().log().all()
                            .statusCode(200).extract()
                            .response().jsonPath().getList("$").size();
                }),
                dynamicTest("테마을 추가한다.", () -> {
                    Map<String, String> param = Map.of("name", "테마_테스트",
                            "description", "설명_테스트",
                            "thumbnail", "섬네일");

                    createdId = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(param)
                            .when().post("/admin/themes")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[2];
                }),
                dynamicTest("테마 목록 개수가 1증가한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/themes")
                            .then().log().all()
                            .statusCode(200).body("size()", is(themeSize + 1));
                }),
                dynamicTest("테마이름이 비어있을 수 없다.", () -> {
                    Map<String, String> param = Map.of("name", "  ",
                            "description", "설명_테스트",
                            "thumbnail", "섬네일");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(param)
                            .when().post("/admin/themes")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("테마를 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/themes/" + createdId)
                            .then().log().all()
                            .statusCode(204);
                }),
                dynamicTest("테마 목록 개수가 1감소한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/themes")
                            .then().log().all()
                            .statusCode(200).body("size()", is(themeSize));
                })
        );
    }

    @DisplayName("인기 테마 조회")
    @Test
    void themeNameBlank() {
        Map<String, String> params = Map.of("startDate", "2024-05-04",
                "endDate", "2024-05-09",
                "limit", "2");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .queryParams(params)
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200);
    }
}
