package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.application.reservation.dto.request.ReservationPaymentRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationWaitingResponse;
import roomescape.exception.UnAuthorizedException;
import roomescape.exception.reservation.DuplicatedReservationException;

class ReservationWaitingDocsTest extends RestDocsTest {

    @Test
    @DisplayName("예약 대기를 생성 한다.")
    void postSuccess() {
        LocalDate date = LocalDate.parse("2025-11-01");
        ReservationResponse reservationResponse = new ReservationResponse(1L, "wiib", "테마", date, LocalTime.of(10, 20));
        ReservationWaitingResponse response = new ReservationWaitingResponse(reservationResponse, 1);

        doReturn(response)
                .when(reservationService)
                .enqueueWaitingList(any());

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                1L, 1L, date, 1L, "paymentKey", "orderId"
        );

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations/queue")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .apply(document("/waiting/post/success"
                        , requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("예약 대기자 식별자"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("대기하려는 방탈출 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("대기하려는 방탈출 시간 식별자"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 정보 식별자"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 정보 식별자")
                        )
                        , responseFields(
                                fieldWithPath("reservation").type(JsonFieldType.OBJECT).description("예약 관련 정보를 담은 객체"),
                                fieldWithPath("waitingCount").type(JsonFieldType.NUMBER).description("예약 대기 순위")
                        ).andWithPrefix("reservation.",
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("대기하려는 예약의 식별자"),
                                fieldWithPath("member").type(JsonFieldType.STRING).description("대기하려는 예약의 예약자명"),
                                fieldWithPath("theme").type(JsonFieldType.STRING).description("대기하려는 예약의 테마"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("대기하려는 예약의 방탈출 날짜"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("대기하려는 예약의 방탈출 시작 시간")
                        )
                ));
    }

    @Test
    @DisplayName("예약 대기 생성을 실패한다.")
    void postFail() {
        LocalDate date = LocalDate.parse("2025-11-01");

        doThrow(new DuplicatedReservationException(1L, date, 1L))
                .when(reservationService)
                .enqueueWaitingList(any());

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                1L, 1L, date, 1L, "paymentKey", "orderId"
        );

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations/queue")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/waiting/post/fail"));
    }

    @Test
    @DisplayName("예약 대기를 삭제한다.")
    void deleteSuccess() {

        doNothing()
                .when(reservationService)
                .cancelWaitingList(any(Long.class), any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().delete("/reservations/queue/" + 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .apply(document("/waiting/delete/success"));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 예약대기를 삭제한다.")
    void deleteFail() {

        doThrow(new UnAuthorizedException())
                .when(reservationService)
                .cancelWaitingList(any(Long.class), any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().delete("/reservations/queue/" + 1L)
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .apply(document("/waiting/delete/fail"));
    }

    @Test
    @DisplayName("관리자가 예약 대기를 조회한다.")
    void getAllSuccess() {
        LocalDate date = LocalDate.parse("2025-11-01");
        List<ReservationResponse> responses = List.of(
                new ReservationResponse(1L, "wiib", "테마", date, LocalTime.of(10, 20)),
                new ReservationResponse(2L, "wiib", "테마2", date, LocalTime.of(12, 20))
        );

        doReturn(responses)
                .when(reservationLookupService)
                .findAllWaitingReservations();

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().get("/reservations/queue")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/waiting/get/all/success",
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 식별자"),
                                fieldWithPath("[].member").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("[].theme").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약한 방탈출 날짜"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약한 방탈출 시작 시간")
                        )
                ));
    }

    @Test
    @DisplayName("회원이 예약 대기를 조회하면 실패한다.")
    void getAllFail() {
        LocalDate date = LocalDate.parse("2025-11-01");
        List<ReservationResponse> responses = List.of(
                new ReservationResponse(1L, "wiib", "테마", date, LocalTime.of(10, 20)),
                new ReservationResponse(2L, "wiib", "테마2", date, LocalTime.of(12, 20))
        );

        doReturn(responses)
                .when(reservationLookupService)
                .findAllWaitingReservations();

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get("/reservations/queue")
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .apply(document("/waiting/get/all/fail"));
    }
}
