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

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.application.auth.dto.TokenPayload;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationWaitingResponse;
import roomescape.domain.member.Role;
import roomescape.exception.reservation.DuplicatedReservationException;

class ReservationWaitingControllerTest extends ControllerTest {

    @BeforeEach
    void setUp() {
        BDDMockito.given(tokenManager.extract(anyString()))
                .willReturn(new TokenPayload(1L, "아루", Role.MEMBER));
    }

    @Test
    @DisplayName("이미 예약된 곳에 대기한다.")
    void enqueueWaitList() {
        BDDMockito.given(reservationService.enqueueWaitingList(any(ReservationRequest.class)))
                .willReturn(new ReservationWaitingResponse(
                        new ReservationResponse(1L, "아루", "테마", LocalDate.of(2024, 6, 12), LocalTime.of(10, 0)),
                        2
                ));
        String request = """
                {
                    "themeId": 1,
                    "date": "2024-06-12",
                    "timeId": 3
                }
                """;

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("reservation.id").description("예약 ID"),
                fieldWithPath("reservation.member").description("대기자 이름"),
                fieldWithPath("reservation.theme").description("테마 이름"),
                fieldWithPath("reservation.date").description("예약 날짜"),
                fieldWithPath("reservation.startAt").description("시작 시간"),
                fieldWithPath("waitingCount").description("대기자 수")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-waiting-enqueue",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "auth-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/reservations/queue")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    @DisplayName("이미 예약한 곳에 대기할 수 없다.")
    void duplicateWaiting() {
        BDDMockito.given(reservationService.enqueueWaitingList(any(ReservationRequest.class)))
                .willThrow(DuplicatedReservationException.class);
        String request = """
                {
                    "themeId": 1,
                    "date": "2024-06-12",
                    "timeId": 3
                }
                """;

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-waiting-enqueue-already-reserved",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "auth-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/reservations/queue")
                .then().log().all()
                .apply(handler)
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 대기를 취소한다.")
    void dequeueWaitList() {
        BDDMockito.doNothing()
                .when(reservationService)
                .cancelWaitingList(anyLong(), anyLong());

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        ParameterDescriptor[] pathParameterDescriptors = {
                parameterWithName("id").description("예약 대기 ID")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-waiting-dequeue",
                requestCookies(cookieDescriptors),
                pathParameters(pathParameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "auth-token")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .pathParam("id", 1L)
                .when().delete("/reservations/queue/{id}")
                .then().log().all()
                .apply(handler)
                .statusCode(204);
    }
}
