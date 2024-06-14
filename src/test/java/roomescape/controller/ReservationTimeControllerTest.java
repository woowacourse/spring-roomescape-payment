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
import roomescape.controller.config.ControllerTestSupport;

import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

class ReservationTimeControllerTest extends ControllerTestSupport {

    String createdId;
    int timeSize;

    @Test
    @DisplayName("예약 시간 목록 조회")
    void showReservationTime() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 배열"),
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("시간"));
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, responseFields))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().get("/times")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 시간 추가")
    void saveReservationTime() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));
        RequestFieldsSnippet requestFields = requestFields(
                fieldWithPath("startAt").type(JsonFieldType.STRING).description("시간"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("startAt").type(JsonFieldType.STRING).description("시간"));

        Map<String, String> param = Map.of("startAt", "12:12");
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, requestFields, responseFields))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .body(param)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약 시간 삭제")
    void deleteReservationTime() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));

        Map<String, String> param = Map.of("startAt", "13:13");
        String createdId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .body(param)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201).extract().header("location").split("/")[2];

        RestAssured.given(specification).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(makeDocumentFilter(requestCookies))
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/admin/times/" + createdId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("예약 시간 CRUD")
    @TestFactory
    Stream<DynamicTest> dynamicUserTestsFromCollection() {
        return Stream.of(
                dynamicTest("예약 시간 목록을 조회한다.", () -> {
                    timeSize = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/times")
                            .then().log().all()
                            .statusCode(200).extract()
                            .response().jsonPath().getList("$").size();
                }),
                dynamicTest("예약 시간을 추가한다.", () -> {
                    Map<String, String> param = Map.of("startAt", "12:12");

                    createdId = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(param)
                            .when().post("/admin/times")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[2];
                }),
                dynamicTest("예약 시간 목록 개수가 1증가한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/times")
                            .then().log().all()
                            .statusCode(200).body("size()", is(timeSize + 1));
                }),
                dynamicTest("유효하지 않은 형식으로 시간을 추가할 수 없다.", () -> {
                    Map<String, String> param = Map.of("startAt", "12:12:12");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(param)
                            .when().post("/admin/times")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("시간을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/times/" + createdId)
                            .then().log().all()
                            .statusCode(204);
                }),
                dynamicTest("예약 시간 목록 개수가 1감소한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/times")
                            .then().log().all()
                            .statusCode(200).body("size()", is(timeSize));
                })
        );
    }
}
