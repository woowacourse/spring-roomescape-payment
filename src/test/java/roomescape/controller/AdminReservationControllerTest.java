package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;
import roomescape.controller.config.ControllerTestSupport;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

class AdminReservationControllerTest extends ControllerTestSupport {

    @DisplayName("예약 내역을 필터링하여 조회한다.")
    @Test
    void findReservationByFilter() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));
        QueryParametersSnippet requestParams = queryParameters(
                parameterWithName("themeId").description("테마 키"),
                parameterWithName("memberId").description("회원 키"),
                parameterWithName("dateFrom").description("시작 날짜"),
                parameterWithName("dateTo").description("끝 날짜"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 배열"),
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("[].date").type(JsonFieldType.STRING).description("추가 날짜"),
                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("회원"),
                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("회원 - 키"),
                fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("회원 - 이름"),
                fieldWithPath("[].member.role").type(JsonFieldType.STRING).description("회원 - 역할"),
                fieldWithPath("[].time").type(JsonFieldType.OBJECT).description("시간"),
                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("시간 - 키"),
                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("시간 - 시작 시간"),
                fieldWithPath("[].theme").type(JsonFieldType.OBJECT).description("테마"),
                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 - 키"),
                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 - 이름"),
                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 - 설명"),
                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING).description("테마 - 썸네일"),
                fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 혹은 대기 상태"));

        Map<String, String> params = Map.of(
                "themeId", "1",
                "memberId", "1",
                "dateFrom", "2000-01-01",
                "dateTo", "9999-09-09"
        );

        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, requestParams, responseFields))
                .accept(ContentType.JSON)
                .cookies("token", ADMIN_TOKEN)
                .queryParams(params)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));
    }

    @DisplayName("예약 대기 목록을 조회한다.")
    @Test
    void findAllWaiting() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 배열"),
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("[].name").type(JsonFieldType.STRING).description("회원 이름"),
                fieldWithPath("[].theme").type(JsonFieldType.STRING).description("테마 이름"),
                fieldWithPath("[].date").type(JsonFieldType.STRING).description("날짜"),
                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("시간"));
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, responseFields))
                .accept(ContentType.JSON)
                .cookies("token", ADMIN_TOKEN)
                .when().get("/admin/reservations/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));
    }
}
