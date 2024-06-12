package roomescape.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.application.auth.dto.TokenPayload;
import roomescape.application.reservation.dto.request.ReservationFilterRequest;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.domain.member.Role;

class AdminReservationControllerTest extends ControllerTest {

    @BeforeEach
    void setUp() {
        BDDMockito.doNothing()
                .when(credentialContext)
                .validatePermission(Role.ADMIN);
        BDDMockito.given(tokenManager.extract(anyString()))
                .willReturn(new TokenPayload(1L, "어드민", Role.ADMIN));
    }

    @Test
    @DisplayName("관리자가 예약을 추가한다.")
    void createReservationByAdmin() {
        LocalDate date = LocalDate.of(2024, 6, 1);
        ReservationRequest request = new ReservationRequest(1L, 1L, date, 3L);

        BDDMockito.given(reservationService.bookReservationWithoutPurchase(request))
                .willReturn(new ReservationResponse(1L, "아루", "테마", date, LocalTime.of(10, 0)));

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

        RestDocumentationResultHandler handler = document(
                "reservation-create",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptor),
                responseFields(responseFieldDescriptor)
        );

        givenWithSpec().log().all()
                .cookie("token", "admin-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/admin/reservations")
                .then().log().all()
                .apply(handler)
                .statusCode(201);
    }

    @Test
    @DisplayName("관리자가 예약을 필터링해 조회한다.")
    void reservationFilter() {
        List<ReservationResponse> responses = List.of(
                new ReservationResponse(1L, "아루", "테마 1", LocalDate.of(2024, 6, 1), LocalTime.of(10, 0)),
                new ReservationResponse(2L, "아루", "테마 2", LocalDate.of(2024, 6, 2), LocalTime.of(13, 0))
        );
        BDDMockito.given(reservationLookupService.findByFilter(any(ReservationFilterRequest.class)))
                .willReturn(responses);

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

        RestDocumentationResultHandler handler = document(
                "reservation-filter",
                requestCookies(cookieDescriptors),
                queryParameters(parameterDescriptors),
                responseFields(responseFieldDescriptor)
        );

        givenWithSpec().log().all()
                .cookie("token", "admin-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .queryParam("memberId", 1L)
                .queryParam("startDate", "2024-06-01")
                .queryParam("endDate", "2024-06-02")
                .when().get("/admin/reservations")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    @DisplayName("관리자가 예약을 삭제한다.")
    void deleteReservation() {
        BDDMockito.doNothing()
                .when(reservationService)
                .cancelReservation(anyLong(), anyLong());

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        ParameterDescriptor[] parameterDescriptors = {
                parameterWithName("id").description("예약 ID")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-delete",
                requestCookies(cookieDescriptors),
                pathParameters(parameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "admin-token")
                .pathParam("id", 1L)
                .when().delete("/reservations/{id}")
                .then().log().all()
                .apply(handler)
                .statusCode(204);
    }
}
