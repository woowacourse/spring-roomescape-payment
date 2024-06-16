package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.request.ReservationTimeRequest;
import roomescape.model.ReservationTime;

import java.time.LocalTime;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class ReservationTimeControllerTest extends AbstractControllerTest {
    @DisplayName("모든 예약 시간을 조회한다")
    @Test
    void should_get_reservation_times() {
        RestDocumentationFilter description = document("times-success-get",
                responseFields(
                        fieldWithPath("[].id").description("예약 가능 시간 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].startAt").description("예약 가능 시간").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().get("/times")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ReservationTime.class);
    }

    @DisplayName("예약 시간을 추가한다")
    @Test
    void should_add_reservation_times() {
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(16, 0));
        RestDocumentationFilter description = document("times-success-post",
                requestFields(fieldWithPath("startAt").description("등록할 시간 지정 (포맷 : `HH:mm`)")),
                responseFields(fieldWithPath("id").description("생성된 시간 Id"),
                        fieldWithPath("startAt").description("생성된 시간 값")
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/times/7");
    }

    @DisplayName("예약 시간을 삭제한다")
    @Test
    void should_remove_reservation_time() {
        RestDocumentationFilter description = document("times-success-delete",
                pathParameters(
                        parameterWithName("id").description("삭제할 시간 id")
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().delete("/times/{id}", 5)
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("특정 날짜와 테마에 따른 모든 시간의 예약 가능 여부를 확인한다.")
    @Test
    void should_get_reservations_with_book_state_by_date_and_theme() {
        RestDocumentationFilter description = document("times-reserved-success-get",
                queryParameters(
                        parameterWithName("date").description("검색할 예약 날짜"),
                        parameterWithName("themeId").description("검색할 시간 Id")
                ),
                responseFields(
                        fieldWithPath("[].timeId").description("예약 시간 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].startAt").description("예약 시간").type(JsonFieldType.STRING),
                        fieldWithPath("[].alreadyBooked").description("해당 날짜와 시간에 대한 예약 여부").type(JsonFieldType.BOOLEAN)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().get("/times/reserved?date=2030-08-05&themeId=1")
                .then().log().all()
                .statusCode(200);
    }
}
