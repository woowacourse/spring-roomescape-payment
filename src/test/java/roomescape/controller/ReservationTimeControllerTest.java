package roomescape.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.application.reservation.dto.request.ReservationTimeRequest;
import roomescape.application.reservation.dto.response.AvailableTimeResponse;
import roomescape.application.reservation.dto.response.ReservationTimeResponse;
import roomescape.domain.member.Role;

class ReservationTimeControllerTest extends ControllerTest {

    @Test
    @DisplayName("관리자가 예약 시간을 생성한다.")
    void createReservationTimeTest() {
        BDDMockito.doNothing()
                .when(credentialContext)
                .validatePermission(Role.ADMIN);
        BDDMockito.given(reservationTimeService.create(any(ReservationTimeRequest.class)))
                .willReturn(new ReservationTimeResponse(1L, LocalTime.of(10, 0)));

        String request = """
                {
                    "startAt": "10:00"
                }
                """;

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("startAt").description("예약 시각"),
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("id").description("ID"),
                fieldWithPath("startAt").description("예약 시각")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-time-create",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "admin-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .apply(handler)
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("예약 시간을 모두 조회한다.")
    void findAllReservationTimesTest() {
        List<ReservationTimeResponse> responses = List.of(
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ReservationTimeResponse(2L, LocalTime.of(11, 30))
        );
        BDDMockito.given(reservationTimeService.findAll())
                .willReturn(responses);

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("ID"),
                fieldWithPath("[].startAt").description("예약 시각")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-time-find-all",
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .when().get("/times")
                .then().log().all()
                .apply(handler)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("관리자가 예약 시간을 삭제한다.")
    void deleteReservationTimeTest() {
        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        ParameterDescriptor[] parameterDescriptors = {
                parameterWithName("id").description("예약 시간 ID")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-time-delete",
                requestCookies(cookieDescriptors),
                pathParameters(parameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "admin-token")
                .pathParam("id", 1L)
                .when().delete("/times/{id}")
                .then().log().all()
                .apply(handler)
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("예약 가능한 시간을 조회한다.")
    void findAvailableTimesTest() {
        List<AvailableTimeResponse> responses = List.of(
                new AvailableTimeResponse(new ReservationTimeResponse(1L, LocalTime.of(10, 0)), false),
                new AvailableTimeResponse(new ReservationTimeResponse(2L, LocalTime.of(13, 30)), true)
        );
        BDDMockito.given(reservationTimeService.findAvailableTimes(any(LocalDate.class), anyLong()))
                .willReturn(responses);

        ParameterDescriptor[] requestParameterDescriptors = {
                parameterWithName("date").description("예약 날짜"),
                parameterWithName("themeId").description("테마 ID")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].time.id").description("시간 ID"),
                fieldWithPath("[].time.startAt").description("예약 시각"),
                fieldWithPath("[].isBooked").description("예약 여부")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-time-find-available",
                queryParameters(requestParameterDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .queryParam("date", "2024-12-25")
                .queryParam("themeId", 1L)
                .when().get("/times/available")
                .then().log().all()
                .apply(handler)
                .statusCode(HttpStatus.OK.value());
    }
}
