package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.exception.NotFoundException;
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
        LocalDate date = LocalDate.now().plusDays(1);
        long themeId = 2L;
        AvailableTimeResponse availableTimeResponse1 = new AvailableTimeResponse(1L, LocalTime.NOON, true);
        AvailableTimeResponse availableTimeResponse2 = new AvailableTimeResponse(2L, LocalTime.MIDNIGHT, false);

        //when
        doReturn(List.of(availableTimeResponse1, availableTimeResponse2))
                .when(reservationTimeService)
                .findAvailableTimes(any(), isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get(String.format("/api/v1/times/available?date=%s&themeId=%d", date, themeId))
                .then().log().all()
                .apply(document("available-times/find/success",
                        responseFields(
                                fieldWithPath("[].timeId").description("예약 시간의 식별자"),
                                fieldWithPath("[].startAt").description("예약 시간"),
                                fieldWithPath("[].alreadyBooked").description("가능 여부")
                        )))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("과거 시간의 가능한 예약 시간 검색 시, 400을 반환한다.")
    @Test
    void getAvailable_invalidDate() {
        //given
        LocalDate date = LocalDate.EPOCH;
        long themeId = 2L;

        //when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get(String.format("/api/v1/times/available?date=%s&themeId=%d", date, themeId))
                .then().log().all()
                .apply(document("available-times/find/fail/invalid-date"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 테마의 가능한 예약 시간 검색 시, 404를 반환한다.")
    @Test
    void getAvailable_invalidTheme() {
        //given
        LocalDate date = LocalDate.now().plusDays(1);
        long themeId = 99999999999L;

        //when
        doThrow(new NotFoundException(ErrorType.THEME_NOT_FOUND))
                .when(reservationTimeService)
                .findAvailableTimes(any(), isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get(String.format("/api/v1/times/available?date=%s&themeId=%d", date, themeId))
                .then().log().all()
                .apply(document("available-times/find/fail/theme-not-found"))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
