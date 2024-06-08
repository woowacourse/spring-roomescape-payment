package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.controller.request.WaitingRequest;

import java.time.LocalDate;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class WaitingControllerTest extends AbstractControllerTest {

    @DisplayName("예약 대기를 추가할 수 있다.")
    @Test
    void should_add_waiting_when_request() {
        RestDocumentationFilter description = document("waiting-success-post",
                requiredCookie,
                requestFields(
                        fieldWithPath("date").description("예약 대기 할 날짜"),
                        fieldWithPath("timeId").description("예약 대기 할 시간 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("themeId").description("예약 대기 할 테마 Id").type(JsonFieldType.NUMBER)
                ),
                responseFields(
                        fieldWithPath("id").description("예약 대기 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("name").description("예약 대기한 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("date").description("등록한 예약 대기 날짜").type(JsonFieldType.STRING),
                        fieldWithPath("time").description("등록한 예약 대기 시간").type(JsonFieldType.OBJECT),
                        fieldWithPath("time.id").description("예약 대기 시간 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("time.startAt").description("예약 대기 시간").type(JsonFieldType.STRING),
                        fieldWithPath("theme").description("등록한 예약 대기 테마").type(JsonFieldType.OBJECT),
                        fieldWithPath("theme.id").description("테마 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("theme.name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("theme.description").description("테마 상세 설명").type(JsonFieldType.STRING),
                        fieldWithPath("theme.thumbnail").description("테마 이미지 URL").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("생성된 예약 대기 Id가 reservation/`id` 형태로 응답")
                )
        );
        WaitingRequest request = new WaitingRequest(
                LocalDate.of(2030, 8, 5), 6L, 10L);
        RestAssured.given(spec).log().all()
                .filter(description)
                .contentType(ContentType.JSON)
                .body(request)
                .cookie("token", getMemberCookie())
                .when().post("/waiting")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/waiting/1");
    }

    @DisplayName("존재하는 예약 대기라면 예약 대기를 삭제할 수 있다.")
    @Test
    void should_delete_waiting_when_waiting_exist() {
        RestDocumentationFilter description = document("waiting-success-delete",
                pathParameters(
                        parameterWithName("id").description("삭제할 예약 대기 id")
                )
        );

        WaitingRequest request = new WaitingRequest(
                LocalDate.of(2030, 8, 5), 6L, 10L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie("token", getMemberCookie())
                .when().post("/waiting")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/waiting/1");


        RestAssured.given(spec).log().all()
                .filter(description)
                .when().delete("/waiting/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("예약 대기를 조회한다.")
    @Test
    void should_get_waiting() {
        RestDocumentationFilter description = document("waitings-success-get",
                responseFields(
                        fieldWithPath("[].id").description("예약 대기 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].name").description("예약 대기한 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].date").description("등록한 예약 대기 날짜").type(JsonFieldType.STRING),
                        fieldWithPath("[].time").description("등록한 예약 대기 시간").type(JsonFieldType.OBJECT),
                        fieldWithPath("[].time.id").description("예약 대기 시간 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].time.startAt").description("예약 대기 시간").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme").description("등록한 예약 대기 테마").type(JsonFieldType.OBJECT),
                        fieldWithPath("[].theme.id").description("테마 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].theme.name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme.description").description("테마 상세 설명").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme.thumbnail").description("테마 이미지 URL").type(JsonFieldType.STRING)
                )
        );
        WaitingRequest request = new WaitingRequest(
                LocalDate.of(2030, 8, 5), 6L, 10L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie("token", getMemberCookie())
                .when().post("/waiting")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/waiting/1");

        RestAssured.given(spec).log().all()
                .filter(description)
                .when().get("/waitings")
                .then().log().all()
                .statusCode(200).extract();
    }
}
