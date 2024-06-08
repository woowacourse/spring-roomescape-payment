package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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
                .apply(document("/waiting/post/success"));
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
                .apply(document("/waiting/get/all/success"));
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
