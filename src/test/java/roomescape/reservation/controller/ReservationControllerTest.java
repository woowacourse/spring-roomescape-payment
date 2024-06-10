package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static roomescape.fixture.DateFixture.getNextDay;

import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.AuthorizationException;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.ReservationTimeResponse;
import roomescape.reservation.controller.dto.ThemeResponse;
import roomescape.reservation.service.dto.MemberReservationCreate;
import roomescape.util.ControllerTest;

@DisplayName("예약 API 통합 테스트")
class ReservationControllerTest extends ControllerTest {

    @DisplayName("사용자 예약 생성 시 201을 반환한다.")
    @Test
    void create() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse,
                getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2099-08-05");
        params.put("timeId", reservationTimeResponse.id());
        params.put("themeId", themeResponse.id());
        params.put("paymentKey", "payemnt-key");
        params.put("orderId", "orderId-123");
        params.put("amount", 15000L);

        //when
        doReturn(reservationResponse)
                .when(reservationApplicationService)
                .createMemberReservation(any(MemberReservationCreate.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .body(params)
                .when().post("/api/v1/reservations")
                .then().log().all()
                .apply(document("reservations/create/success",
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제의 키 값입니다. 결제를 식별하는 역할로, 중복되지 않는 고유한 값입니다."),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문번호입니다. 주문한 결제를 식별합니다. 충분히 무작위한 값을 생성해서 각 주문마다 고유한 값을 넣어주세요."),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제할 금액")
                        )))
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("예약 삭제 시 204를 반환한다.")
    @Test
    void deleteTest() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse,
                getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        //when
        doNothing()
                .when(reservationApplicationService)
                .deleteMemberReservation(isA(AuthInfo.class), isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when().delete("/api/v1/reservations/" + reservationResponse.memberReservationId())
                .then().log().all()
                .apply(document("reservations/delete/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("타인의 예약 삭제 시, 403을 반환한다.")
    @Test
    void delete_InvalidUser() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse,
                getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        //when
        doThrow(new AuthorizationException(ErrorType.NOT_A_RESERVATION_MEMBER))
                .when(reservationApplicationService)
                .deleteMemberReservation(isA(AuthInfo.class), isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when().delete("/api/v1/reservations/" + reservationResponse.memberReservationId())
                .then().log().all()
                .apply(document("reservations/delete/fail/invalid-auth"))
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @DisplayName("예약 조회 시 200을 반환한다.")
    @Test
    void find() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse,
                getNextDay(),
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
                .cookie("token", memberToken)
                .when().get("/api/v1/reservations")
                .then().log().all()
                .apply(document("reservations/find/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("지나간 날짜와 시간에 대한 예약 생성 시, 400을 반환한다.")
    @Test
    void createReservationAfterNow() {
        //given
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));

        Map<String, Object> params = new HashMap<>();
        params.put("date", LocalDate.now().minusDays(2).toString());
        params.put("timeId", reservationTimeResponse.id());
        params.put("themeId", themeResponse.id());

        //when
        doThrow(new BadRequestException(ErrorType.INVALID_REQUEST_ERROR))
                .when(reservationApplicationService)
                .createMemberReservation(any(MemberReservationCreate.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .body(params)
                .when().post("/api/v1/reservations")
                .then().log().all()
                .apply(document("reservations/create/fail/invalid-date-time"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("예약 대기 시, 201을 반환한다.")
    @Test
    void createWaiting() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse,
                getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        Map<String, Object> params = new HashMap<>();
        params.put("date", reservationResponse.date().toString());
        params.put("timeId", reservationResponse.time().id());
        params.put("themeId", reservationResponse.theme().id());

        //when
        doReturn(reservationResponse)
                .when(reservationApplicationService)
                .addWaiting(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .body(params)
                .when().post("/api/v1/reservations/waiting")
                .then().log().all()
                .apply(document("waiting/create/success,",
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 식별자")
                        )))
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("예약 대기 삭제 시, 204을 반환한다.")
    @Test
    void deleteWaiting() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse(2L, LocalTime.NOON);
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(4L,
                memberResponse,
                getNextDay(),
                reservationTimeResponse,
                themeResponse
        );

        //when
        doNothing()
                .when(reservationApplicationService)
                .deleteWaiting(any(), anyLong());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when()
                .delete(String.format("/api/v1/reservations/%d/waiting", reservationResponse.memberReservationId()))
                .then().log().all()
                .apply(document("waiting/delete/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("대기 등록한 사용자가 아닌 타사용자가 취소 시, 403을 반환한다.")
    @Test
    void delete_NotReservationMember() {
        //given & when
        doThrow(new AuthorizationException(ErrorType.NOT_A_RESERVATION_MEMBER))
                .when(reservationApplicationService)
                .deleteWaiting(any(), anyLong());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when()
                .delete(String.format("/api/v1/reservations/%d/waiting", 3))
                .then().log().all()
                .apply(document("waiting/delete/fail/invalid-auth"))
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
