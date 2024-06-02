package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.reservation.controller.dto.AvailableTimeResponse;
import roomescape.reservation.controller.dto.ReservationTimeResponse;
import roomescape.util.ControllerTest;

@DisplayName("예약 시간 API 통합 테스트")
class ReservationTimeControllerTest extends ControllerTest {

    @DisplayName("예약 시간 생성 시, 201을 반환한다.")
    @Test
    void create() {
        //given
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);

        Map<String, String> params = new HashMap<>();
        params.put("startAt", "10:00");

        //when
        doReturn(reservationTimeResponse)
                .when(reservationTimeService)
                .create(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .body(params)
                .when().post("/api/v1/times")
                .then().log().all()
                .apply(document("times/create/success"))
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("시간 조회 시, 200을 반환한다.")
    @Test
    void findAll() {
        //given
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);

        //when
        doReturn(List.of(reservationTimeResponse))
                .when(reservationTimeService)
                .findAll();

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/api/v1/times")
                .then().log().all()
                .apply(document("times/find/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("시간 삭제 시, 204을 반환한다.")
    @Test
    void deleteTest() {
        //given
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);

        //when
        doNothing()
                .when(reservationTimeService)
                .delete(isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().delete("/api/v1/times/" + reservationTimeResponse.id())
                .then().log().all()
                .apply(document("times/delete/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("예약이 있는 시간 삭제 시, 400을 반환한다.")
    @Test
    void delete_WithReservationTime() {
        //given
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);

        //when
        doThrow(new BadRequestException(ErrorType.RESERVATION_NOT_DELETED))
                .when(reservationTimeService)
                .delete(isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().delete("/api/v1/times/" + reservationTimeResponse.id())
                .then().log().all()
                .apply(document("times/delete/fail/reservation-exist"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("가능한 시간 조회 시, 200을 반환한다.")
    @Test
    void getAvailable() {
        //given
        AvailableTimeResponse availableTimeResponse = new AvailableTimeResponse(1L, LocalTime.NOON, true);

        //when
        doReturn(List.of(availableTimeResponse))
                .when(reservationTimeService)
                .findAvailableTimes(isA(LocalDate.class), isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/api/v1/times")
                .then().log().all()
                .apply(document("available-times/find/success"))
                .statusCode(HttpStatus.OK.value());
    }
}
