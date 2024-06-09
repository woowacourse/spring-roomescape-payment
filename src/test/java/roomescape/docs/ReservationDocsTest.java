package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.application.reservation.dto.request.ReservationPaymentRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationStatusResponse;
import roomescape.exception.UnAuthorizedException;

class ReservationDocsTest extends RestDocsTest {

    @Test
    @DisplayName("예약을 생성한다.")
    void postSuccess() {
        LocalDate date = LocalDate.parse("2025-11-01");
        ReservationResponse response = new ReservationResponse(1L, "wiib", "테마", date, LocalTime.of(10, 20));

        doReturn(response)
                .when(reservationService)
                .bookReservation(any());

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                1L, 1L, date, 1L, "paymentKey", "orderId"
        );

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .apply(document("/reservation/post/success",
                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 정보 식별자"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 번호 식별자")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 식별자"),
                                fieldWithPath("member").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("theme").type(JsonFieldType.STRING).description("예약한 방탈출 테마명"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약한 방탈출 날짜"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약한 방탈출 시작 시간")
                        )
                ));
    }

    @Test
    @DisplayName("예약을 생성을 실패한다.")
    void postFail() {
        LocalDate date = LocalDate.of(2025, 11, 1);
        doThrow(new IllegalArgumentException("errorMessage"))
                .when(reservationService)
                .bookReservation(any());

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                1L, 1L, date, 1L, "paymentKey", "orderId"
        );

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/reservation/post/fail"));
        ;
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void deleteSuccess() {

        doNothing()
                .when(reservationService)
                .cancelReservation(any(Long.class), any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().delete("/reservations/" + 1)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .apply(document("/reservation/delete/success"));
    }

    @Test
    @DisplayName("내 예약이 아닌 다른 예약을 삭제한다.")
    void deleteFail() {

        doThrow(new UnAuthorizedException())
                .when(reservationService)
                .cancelReservation(any(Long.class), any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().delete("/reservations/" + 1)
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .apply(document("/reservation/delete/fail"));
    }

    @Test
    @DisplayName("관리자가 모든 예약을 조회한다.")
    void getAllSuccess() {
        LocalDate date = LocalDate.of(2025, 11, 1);

        List<ReservationResponse> responses = List.of(
                new ReservationResponse(1L, "wiib1", "테마1", date, LocalTime.of(10, 20)),
                new ReservationResponse(2L, "wiib2", "테마2", date, LocalTime.of(11, 20)),
                new ReservationResponse(3L, "wiib3", "테마3", date, LocalTime.of(12, 20))
        );

        doReturn(responses)
                .when(reservationLookupService)
                .findAllBookedReservations();

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/reservation/get/all/success"
                        , responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 식별자"),
                                fieldWithPath("[].member").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("[].theme").type(JsonFieldType.STRING).description("예약한 방탈출 테마명"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약한 방탈출 날짜"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약한 방탈출 시작 시간")
                        ))
                );
    }

    @Test
    @DisplayName("회원이 모든 예약을 조회하면 실패한다.")
    void getAllFail() {
        LocalDate date = LocalDate.of(2025, 11, 1);

        List<ReservationResponse> responses = List.of(
                new ReservationResponse(1L, "wiib1", "테마1", date, LocalTime.of(10, 20)),
                new ReservationResponse(2L, "wiib2", "테마2", date, LocalTime.of(11, 20)),
                new ReservationResponse(3L, "wiib3", "테마3", date, LocalTime.of(12, 20))
        );

        doReturn(responses)
                .when(reservationLookupService)
                .findAllBookedReservations();

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .apply(document("/reservation/get/all/fail"));
    }

    @Test
    @DisplayName("자신의 모든 예약을 조회한다.")
    void getMeSuccess() {
        LocalDate date = LocalDate.of(2025, 11, 1);

        List<ReservationStatusResponse> responses = List.of(
                new ReservationStatusResponse(1L, "테마", date, LocalTime.of(1, 10), 0, "paymentKey1", 10000L),
                new ReservationStatusResponse(1L, "테마2", date, LocalTime.of(3, 10), 0, "paymentKey2", 20000L),
                new ReservationStatusResponse(1L, "테마3", date, LocalTime.of(5, 10), 0, "paymentKey3", 30000L)

        );

        doReturn(responses)
                .when(reservationLookupService)
                .getReservationStatusesByMemberId(any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get("/reservations/me")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/reservation/get/me/success"
                        , responseFields(
                                fieldWithPath("[].id").description(JsonFieldType.NUMBER).description("예약 식별자"),
                                fieldWithPath("[].theme").description(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("[].date").description(JsonFieldType.STRING).description("예약한 방탈출 날짜"),
                                fieldWithPath("[].time").description(JsonFieldType.STRING).description("예약한 방탈출 시작 시간"),
                                fieldWithPath("[].waitingCount").description(JsonFieldType.NUMBER)
                                        .description("예약 대기순위"),
                                fieldWithPath("[].paymentKey").description(JsonFieldType.STRING).description("결제 식별자"),
                                fieldWithPath("[].amount").description(JsonFieldType.NUMBER).description("결제 금액")
                        )
                ));
    }

    @Test
    @DisplayName("관리자가 조건에 맞는 예약을 조회한다.")
    void getByFilterSuccess() {
        LocalDate date = LocalDate.of(2025, 11, 1);

        List<ReservationResponse> responses = List.of(
                new ReservationResponse(1L, "wiib", "테마1", date, LocalTime.of(10, 20)),
                new ReservationResponse(2L, "wiib", "테마2", date, LocalTime.of(11, 20)),
                new ReservationResponse(3L, "wiib", "테마3", date, LocalTime.of(12, 20))
        );

        doReturn(responses)
                .when(reservationLookupService)
                .findByFilter(any());

        Map<String, Object> queryParam = Map.of(
                "memberId", 1L,
                "themeId", 1L,
                "startDate", "2025-11-01",
                "endDate", "2025-11-30"
        );

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .contentType(ContentType.JSON)
                .queryParams(queryParam)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/reservation/get/filter/success",
                        queryParameters(
                                parameterWithName("startDate").description("필터링 조건: 시간 날짜(nullable)"),
                                parameterWithName("endDate").description("필터링 조건: 끝 날짜(nullable)"),
                                parameterWithName("themeId").description("필터링 조건: 테마 식별자(nullable)"),
                                parameterWithName("memberId").description("필터링 조건: 회원 식별자(nullable)")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 식별자"),
                                fieldWithPath("[].member").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("[].theme").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약한 방탈출 날짜"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약한 방탈출 시작 시간")
                        )
                ));
    }
}
