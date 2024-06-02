package roomescape.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
import org.springframework.http.MediaType;
import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.domain.AuthInfo;
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
                .when().delete(String.format("/api/v1/admin/reservations/%d", reservationResponse.memberReservationId()))
                .then().log().all()
                .apply(document("admin-reservations/delete/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
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
                .post(String.format("/api/v1/admin/reservations/%d/waiting/approve", waitingResponse.memberReservationId()))
                .then().log().all()
                .apply(document("approve/change/success"))
                .statusCode(HttpStatus.OK.value());
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
                .post(String.format("/api/v1/admin/reservations/%d/waiting/deny", waitingResponse.memberReservationId()))
                .then().log().all()
                .apply(document("deny/change/success"))
                .statusCode(HttpStatus.OK.value());
    }
}
