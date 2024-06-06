package roomescape.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static roomescape.fixture.DateFixture.getNextDay;

import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.exception.NotFoundException;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.ReservationTimeResponse;
import roomescape.reservation.controller.dto.ThemeResponse;
import roomescape.util.ControllerTest;

@DisplayName("관리자 API 통합 테스트")
class AdminControllerTest extends ControllerTest {

    @DisplayName("예약 목록 조회 시, 200을 반환한다.")
    @Test
    void getReservations() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse, getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        //when
        doReturn(List.of(reservationResponse))
                .when(reservationApplicationService)
                .findMemberReservations(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/api/v1/reservations")
                .then().log().all()
                .apply(document("admin-reservations/find/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자 예약 생성 시, 201을 반환한다.")
    @Test
    void create() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse, getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberResponse.id());
        params.put("date", "2099-08-05");
        params.put("timeId", reservationTimeResponse.id());
        params.put("themeId", themeResponse.id());

        //when
        doReturn(reservationResponse)
                .when(reservationApplicationService)
                .createMemberReservation(any(AdminMemberReservationRequest.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .body(params)
                .when().post("/api/v1/admin/reservations")
                .then().log().all()
                .apply(document("admin-reservations/create/success"))
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("존재하지 않는 시간, 멤버, 테마 생성 시, 404를 반환한다.")
    @Test
    void invalidTimeId() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", 1);
        params.put("date", "2099-08-05");
        params.put("timeId", 999999999);
        params.put("themeId", 2);

        //when
        doThrow(new NotFoundException(ErrorType.RESERVATION_TIME_NOT_FOUND))
                .when(reservationApplicationService)
                .createMemberReservation(any(AdminMemberReservationRequest.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .body(params)
                .when().post("/api/v1/admin/reservations")
                .then().log().all()
                .apply(document("admin-reservations/create/fail/invalid-parameter"))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("관리자 예약 삭제 시, 204를 반환한다.")
    @Test
    void deleteTest() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse, getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        //when
        doNothing()
                .when(reservationApplicationService)
                .delete(isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when()
                .delete(String.format("/api/v1/admin/reservations/%d", reservationResponse.memberReservationId()))
                .then().log().all()
                .apply(document("admin-reservations/delete/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("관리자 예약 삭제 시 예약이 존재하지 않을 경우, 404를 반환한다.")
    @Test
    void delete_notReservationId() {
        //given & when
        doThrow(new NotFoundException(ErrorType.NOT_A_WAITING_RESERVATION))
                .when(reservationApplicationService)
                .delete(anyLong());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when()
                .delete(String.format("/api/v1/admin/reservations/%d", 9999999))
                .then().log().all()
                .apply(document("admin-reservations/delete/reservation-not-found"))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("대기 예약을 승인할 경우, 200을 반환한다.")
    @Test
    void approveWaiting() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse waitingResponse = new ReservationResponse(4L,
                memberResponse, getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        doNothing()
                .when(reservationApplicationService)
                .approveWaiting(isA(AuthInfo.class), isA(Long.class));

        //when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when()
                .patch(String.format("/api/v1/admin/reservations/%d/waiting/approve",
                        waitingResponse.memberReservationId()))
                .then().log().all()
                .apply(document("approve/change/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("예약 승인 시, 대기하는 예약이 아닐 경우 400을 반환한다.")
    @Test
    void approveNotWaitingReservationException() {
        //given & when
        doThrow(new BadRequestException(ErrorType.NOT_A_WAITING_RESERVATION))
                .when(reservationApplicationService)
                .approveWaiting(any(), anyLong());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when()
                .patch(String.format("/api/v1/admin/reservations/%d/waiting/approve", 1))
                .then().log().all()
                .apply(document("approve/change/fail/not-waiting"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("대기 예약을 거절할 경우, 200을 반환한다.")
    @Test
    void denyWaiting() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse waitingResponse = new ReservationResponse(4L,
                memberResponse, getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        //when
        doNothing()
                .when(reservationApplicationService)
                .approveWaiting(isA(AuthInfo.class), isA(Long.class));

        //when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when()
                .patch(String.format("/api/v1/admin/reservations/%d/waiting/deny", waitingResponse.memberReservationId()))
                .then().log().all()
                .apply(document("deny/change/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("예약 거절 시, 대기하는 예약이 아닐 경우 400을 반환한다.")
    @Test
    void denyNotWaitingReservationException() {
        //given & when
        doThrow(new BadRequestException(ErrorType.NOT_A_WAITING_RESERVATION))
                .when(reservationApplicationService)
                .denyWaiting(any(), anyLong());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when()
                .patch(String.format("/api/v1/admin/reservations/%d/waiting/deny", 1))
                .then().log().all()
                .apply(document("deny/change/fail/not-waiting"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
