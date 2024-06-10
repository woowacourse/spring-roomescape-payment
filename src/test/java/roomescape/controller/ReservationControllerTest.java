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
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.application.auth.dto.TokenPayload;
import roomescape.application.payment.dto.PaymentResponse;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationStatusResponse;
import roomescape.domain.member.Role;
import roomescape.domain.payment.PaymentStatus;

class ReservationControllerTest extends ControllerTest {

    @Test
    @DisplayName("예약을 생성한다.")
    void createReservation() {
        BDDMockito.given(tokenManager.extract(anyString()))
                .willReturn(new TokenPayload(1L, "아루", Role.MEMBER));

        LocalDate date = LocalDate.of(2024, 6, 1);
        LocalTime time = LocalTime.of(10, 0);
        ReservationResponse response = new ReservationResponse(1L, "아루", "테마 1", date, time);
        BDDMockito.given(reservationService.createReservation(any()))
                .willReturn(response);

        CookieDescriptor[] requestCookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID"),
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("paymentKey").description("결제 키"),
                fieldWithPath("orderId").description("주문 ID")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("id").description("예약 ID"),
                fieldWithPath("member").description("예약자 이름"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("startAt").description("예약 시간"),
                fieldWithPath("theme").description("테마")
        };

        RestDocumentationResultHandler handler = document(
                "reservation-create",
                requestCookies(requestCookieDescriptors),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        String request = """
                {
                    "themeId": 1,
                    "date": "2024-12-25",
                    "timeId": 2,
                    "paymentKey": "paymentKey",
                    "orderId": "orderId"
                }
                """;

        givenWithSpec().log().all()
                .cookie("token", "auth-token")
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .when().post("/reservations")
                .then().log().all()
                .apply(handler)
                .statusCode(201);
    }

    @Test
    @DisplayName("자신의 예약을 조회한다.")
    void findMyReservations() {
        BDDMockito.given(tokenManager.extract(anyString()))
                .willReturn(new TokenPayload(1L, "아루", Role.MEMBER));

        List<ReservationStatusResponse> responses = List.of(
                new ReservationStatusResponse(
                        1L, "테마 1", LocalDate.of(2024, 6, 1), LocalTime.of(10, 0), 0,
                        new PaymentResponse("orderId1", "paymentKey1", 15000, PaymentStatus.SUCCESS)
                ),
                new ReservationStatusResponse(
                        2L, "테마 2", LocalDate.of(2024, 6, 2), LocalTime.of(13, 0), 1,
                        null
                )
        );
        BDDMockito.given(reservationLookupService.getReservationStatusesByMemberId(anyLong()))
                .willReturn(responses);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("예약 ID"),
                fieldWithPath("[].date").description("예약 날짜"),
                fieldWithPath("[].theme").description("테마"),
                fieldWithPath("[].startAt").description("예약 시간"),
                fieldWithPath("[].waitingCount").description("대기자 수"),
                fieldWithPath("[].payment").description("결제 정보").optional(),
                fieldWithPath("[].payment.paymentKey").description("결제 키"),
                fieldWithPath("[].payment.orderId").description("주문 ID"),
                fieldWithPath("[].payment.amount").description("결제 금액"),
                fieldWithPath("[].payment.status").description("결제 상태"),
        };

        RestDocumentationResultHandler handler = document(
                "reservation-find-my",
                requestCookies(cookieDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "auth-token")
                .accept(APPLICATION_JSON_VALUE)
                .when().get("/reservations/me")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    @DisplayName("자신의 예약을 삭제한다.")
    void deleteReservation() {
        BDDMockito.given(tokenManager.extract(anyString()))
                .willReturn(new TokenPayload(1L, "아루", Role.MEMBER));
        BDDMockito.doNothing()
                .when(reservationService)
                .cancelReservation(anyLong(), anyLong());

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
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
                .cookie("token", "auth-token")
                .pathParam("id", 1L)
                .when().delete("/reservations/{id}")
                .then().log().all()
                .apply(handler)
                .statusCode(204);
    }
}
