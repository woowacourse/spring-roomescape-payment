package roomescape.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;
import static roomescape.fixture.MemberFixture.MEMBER_PK;
import static roomescape.fixture.ThemeFixture.TEST_THEME;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;

class AdminReservationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("관리자가 예약을 추가한다.")
    void createReservationByAdmin() {
        long memberId = fixture.registerMember(MEMBER_ARU.registerRequest()).id();
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        String adminToken = fixture.getAdminToken();

        LocalDate date = LocalDate.of(2024, 6, 1);
        ReservationRequest request = new ReservationRequest(memberId, themeId, date, timeId);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        FieldDescriptor[] requestFieldDescriptor = {
                fieldWithPath("memberId").description("회원 ID"),
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID")
        };

        FieldDescriptor[] responseFieldDescriptor = {
                fieldWithPath("id").description("예약 ID"),
                fieldWithPath("member").description("예약자 이름"),
                fieldWithPath("theme").description("테마 이름"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("startAt").description("시작 시간")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-create",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptor),
                responseFields(responseFieldDescriptor)
        );

        givenWithSpec().log().all()
                .cookie("token", adminToken)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .body(request)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("관리자가 예약을 필터링해 조회한다.")
    void reservationFilter() {
        long aruId = fixture.registerMember(MEMBER_ARU.registerRequest()).id();
        long pkId = fixture.registerMember(MEMBER_PK.registerRequest()).id();
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long tenAmId = fixture.createReservationTime(10, 0).id();
        long elevenAmId = fixture.createReservationTime(11, 0).id();
        String aruToken = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        String pkToken = fixture.loginAndGetToken(MEMBER_PK.loginRequest());
        fixture.createReservation(aruToken, new ReservationRequest(
                aruId, themeId, LocalDate.of(2024, 6, 1), tenAmId
        ));
        fixture.createReservation(aruToken, new ReservationRequest(
                aruId, themeId, LocalDate.of(2024, 6, 2), tenAmId
        ));
        fixture.createReservation(pkToken, new ReservationRequest(
                pkId, themeId, LocalDate.of(2024, 6, 1), elevenAmId
        ));

        String adminToken = fixture.getAdminToken();

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        ParameterDescriptor[] parameterDescriptors = {
                parameterWithName("memberId").description("회원 ID").optional(),
                parameterWithName("themeId").description("테마 ID").optional(),
                parameterWithName("startDate").description("검색 시작 날짜").optional(),
                parameterWithName("endDate").description("검색 종료 날짜").optional()
        };

        FieldDescriptor[] responseFieldDescriptor = {
                fieldWithPath("[].id").description("예약 ID"),
                fieldWithPath("[].member").description("예약자 이름"),
                fieldWithPath("[].theme").description("테마 이름"),
                fieldWithPath("[].date").description("예약 날짜"),
                fieldWithPath("[].startAt").description("시작 시간")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-filter",
                requestCookies(cookieDescriptors),
                queryParameters(parameterDescriptors),
                responseFields(responseFieldDescriptor)
        );

        givenWithSpec().log().all()
                .cookie("token", adminToken)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .queryParam("memberId", aruId)
                .queryParam("startDate", "2024-06-01")
                .queryParam("endDate", "2024-06-01")
                .when().get("/admin/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    @DisplayName("관리자가 예약을 삭제한다.")
    void deleteReservation() {
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.getAdminToken();
        ReservationResponse response = fixture.createReservation(
                token,
                new ReservationRequest(themeId, LocalDate.of(2024, 12, 25), timeId)
        );

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        ParameterDescriptor[] parameterDescriptors = {
                parameterWithName("id").description("예약 ID")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-delete",
                requestCookies(cookieDescriptors),
                pathParameters(parameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", token)
                .pathParam("id", response.id())
                .filter(docsFilter)
                .when().delete("/reservations/{id}")
                .then().log().all()
                .statusCode(204);
    }
}
