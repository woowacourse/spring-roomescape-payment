package roomescape.presentation.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixtures.anyReservationWithNewId;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import roomescape.application.PaymentService;
import roomescape.application.ReservationService;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.domain.user.UserRole;
import roomescape.exception.NotFoundException;
import roomescape.exception.PaymentFailedException;
import roomescape.exception.PaymentInternalException;
import roomescape.presentation.GlobalExceptionHandler;
import roomescape.presentation.StubAuthenticationInfoArgumentResolver;

class ReservationControllerTest {

    private final long userId = 99L;

    private final ReservationService reservationService = Mockito.mock(ReservationService.class);
    private final PaymentService paymentService = Mockito.mock(PaymentService.class);
    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new ReservationController(reservationService, paymentService))
        .setCustomArgumentResolvers(new StubAuthenticationInfoArgumentResolver(new AuthenticationInfo(userId, UserRole.USER)))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    @Test
    @DisplayName("예약 추가 요청시, id를 포함한 예약 내용과 CREATED를 응답한다.")
    void reserve() throws Exception {
        Mockito.doNothing()
                .when(paymentService).pay(anyString(), anyString(), anyLong());

        Mockito.when(reservationService.reserve(anyLong(), any(), anyLong(), anyLong()))
            .thenReturn(anyReservationWithNewId());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "date": "3000-03-17",
                        "timeId": "1",
                        "themeId": "1",
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(jsonPath("$..['id','user','date','time','theme']").exists())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("결제 승인 실패 시 예약이 생성되지 않는다.")
    void cannotReserveWhenPaymentFailed() throws Exception {
        Mockito.doThrow(new RuntimeException("결제 실패"))
                .when(paymentService).pay(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "date": "3000-03-17",
                        "timeId": "1",
                        "themeId": "1",
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isInternalServerError());

        Mockito.verify(reservationService, never()).reserve(anyLong(), any(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("잘못된 요청으로 결제 승인 실패 시 BAD REQUEST를 응답한다.")
    void cannotReserveWhenBadRequest() throws Exception {
        Mockito.doThrow(new PaymentFailedException("결제에 실패했습니다."))
                .when(paymentService).pay(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "date": "3000-03-17",
                        "timeId": "1",
                        "themeId": "1",
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isBadRequest());

        Mockito.verify(reservationService, never()).reserve(anyLong(), any(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("서버 내부 오류로 결제 승인 실패 시 INTERNAL SERVER ERROR를 응답한다.")
    void cannotReserveWhenInternalServerError() throws Exception {
        Mockito.doThrow(new PaymentInternalException("결제에 실패했습니다."))
                .when(paymentService).pay(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "date": "3000-03-17",
                        "timeId": "1",
                        "themeId": "1",
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isInternalServerError());

        Mockito.verify(reservationService, never()).reserve(anyLong(), any(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("예약 대기 요청시, id를 포함한 예약 내용과 CREATED를 응답한다.")
    void waitFor() throws Exception {
        Mockito.doNothing()
                .when(paymentService).pay(anyString(), anyString(), anyLong());

        Mockito.when(reservationService.waitFor(anyLong(), any(), anyLong(), anyLong()))
            .thenReturn(anyReservationWithNewId());

        mockMvc.perform(post("/reservations/wait")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "date": "3000-03-17",
                        "timeId": "1",
                        "themeId": "1",
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(jsonPath("$..['id','user','date','time','theme']").exists())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("예약 조회 요청시, 조건에 맞는 모든 예약과 OK를 응답한다.")
    void getAllReservations() throws Exception {
        var expectedReservations = List.of(anyReservationWithNewId(), anyReservationWithNewId(), anyReservationWithNewId());
        Mockito.when(reservationService.findAllReservations(any())).thenReturn(expectedReservations);

        mockMvc.perform(get("/reservations"))
            .andExpect(jsonPath("$..['id','user','date','time','theme']").exists())
            .andExpect(jsonPath("$", hasSize(expectedReservations.size())))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일반 유저가 예약 삭제 요청시, FORBIDDEN을 응답한다.")
    void deleteUnauthorized() throws Exception {
        mockMvc.perform(delete("/reservations/1"))
            .andExpect(status().isForbidden());

        Mockito.verify(reservationService, never()).removeById(1L);
    }

    @Test
    @DisplayName("예약 대기 취소 요청시, 주어진 아이디에 해당하는 예약이 있다면 취소하고 NO CONTENT를 응답한다.")
    void cancelWaitingSuccessfully() throws Exception {
        mockMvc.perform(delete("/reservations/wait/1"))
            .andExpect(status().isNoContent());

        Mockito.verify(reservationService, times(1)).cancelWaiting(userId,1L);
    }

    @Test
    @DisplayName("예약 대기 취소 요청시, 주어진 아이디에 해당하는 예약이 없다면 NOT FOUND를 응답한다.")
    void cancelWaitingWhenNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException("should be thrown"))
            .when(reservationService).cancelWaiting(eq(userId), eq(999L));

        mockMvc.perform(delete("/reservations/wait/999"))
            .andExpect(status().isNotFound());
    }
}
