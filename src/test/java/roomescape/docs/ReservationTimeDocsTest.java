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
import roomescape.application.reservation.dto.request.ReservationTimeRequest;
import roomescape.application.reservation.dto.response.AvailableTimeResponse;
import roomescape.application.reservation.dto.response.ReservationTimeResponse;

class ReservationTimeDocsTest extends RestDocsTest {


    @Test
    @DisplayName("관리자가 예약 시간을 생성할 수 있다.")
    void postSuccess() {
        ReservationTimeResponse timeResponse = new ReservationTimeResponse(1L, LocalTime.of(10, 0));

        doReturn(timeResponse)
                .when(reservationTimeService)
                .create(any());

        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(10, 0));

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .apply(document("/times/post/success"));

    }


    @Test
    @DisplayName("관리자가 예약 시간을 생성하면 실패한다.")
    void postFail() {
        doThrow(new IllegalArgumentException("이미 존재하는 시간입니다."))
                .when(reservationTimeService)
                .create(any());

        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(10, 0));

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/times/post/fail"));

    }

    @Test
    @DisplayName("예약 시간을 모두 조회한다.")
    void getAllSuccess() {
        List<ReservationTimeResponse> responses = List.of(
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ReservationTimeResponse(2L, LocalTime.of(12, 30)),
                new ReservationTimeResponse(3L, LocalTime.of(18, 30))
        );

        doReturn(responses)
                .when(reservationTimeService)
                .findAll();

        restDocs
                .when().get("/times")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/times/get/all/success"));

    }

    @DisplayName("관리자가 예약 시간을 삭제한다.")
    @Test
    void deleteSuccess() {
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(1L, LocalTime.of(10, 0));

        doNothing()
                .when(reservationTimeService)
                .deleteById(any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().delete("/times/" + reservationTimeResponse.id())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .apply(document("/times/delete/success"));
    }

    @Test
    @DisplayName("관리자가 예약이 존재하는 예약 시간을 삭제하면 실패한다.")
    void deleteFail() {
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(1L, LocalTime.of(10, 0));

        doThrow(new IllegalArgumentException("연관된 예약이 존재하여 삭제할 수 없습니다."))
                .when(reservationTimeService)
                .deleteById(any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().delete("/times/" + reservationTimeResponse.id())
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/times/delete/fail"));
    }


    @Test
    @DisplayName("예약 가능한 시간을 조회한다.")
    void getAvailableTimes() {
        List<AvailableTimeResponse> availableTimeResponses = List.of(
                new AvailableTimeResponse(new ReservationTimeResponse(1L, LocalTime.of(10, 20)), true),
                new AvailableTimeResponse(new ReservationTimeResponse(2L, LocalTime.of(9, 20)), false)
        );

        doReturn(availableTimeResponses)
                .when(reservationTimeService)
                .findAvailableTimes(LocalDate.of(2025, 11, 10), 1L);

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .contentType(ContentType.JSON)
                .queryParam("date", "2025-11-10")
                .queryParam("themeId", 1L)
                .when().get("/times/available")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/times/get/available/success"));
    }
}
