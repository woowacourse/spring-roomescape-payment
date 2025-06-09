package roomescape.presentation.rest;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import roomescape.exception.PaymentFailedException;
import roomescape.presentation.GlobalExceptionHandler;
import roomescape.presentation.StubAuthenticationInfoArgumentResolver;

public class ReservationControllerPaymentTest {

    private final long userId = 99L;

    private final ReservationService reservationService = Mockito.mock(ReservationService.class);
    private final PaymentService paymentService = Mockito.mock(PaymentService.class);
    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new ReservationController(reservationService, paymentService))
        .setCustomArgumentResolvers(new StubAuthenticationInfoArgumentResolver(new AuthenticationInfo(userId, UserRole.USER)))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    @Test
    @DisplayName("예약에 대한 결제 승인 요청시, OK를 응답한다.")
    void confirmReservation() throws Exception {
        Mockito.doNothing().when(paymentService).confirm(anyLong(), anyString(), anyString(), anyInt());

        mockMvc.perform(post("/reservations/" + 1 + "/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 요청으로 결제 승인 실패 시 BAD REQUEST를 응답한다.")
    void cannotReserveWhenBadRequest() throws Exception {
        Mockito.doThrow(PaymentFailedException.byClient("결제 실패"))
            .when(paymentService).confirm(anyLong(), anyString(), anyString(), anyInt());

        mockMvc.perform(post("/reservations/" + 1 + "/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("서버 내부 오류로 결제 승인 실패 시 INTERNAL SERVER ERROR를 응답한다.")
    void cannotReserveWhenInternalServerError() throws Exception {
        Mockito.doThrow(PaymentFailedException.byServer())
            .when(paymentService).confirm(anyLong(), anyString(), anyString(), anyInt());

        mockMvc.perform(post("/reservations/" + 1 + "/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("외부 서버 오류로 결제 승인 실패 시 INTERNAL SERVER ERROR를 응답한다.")
    void cannotReserveWhenExternalServerError() throws Exception {
        Mockito.doThrow(PaymentFailedException.byExternalServer())
            .when(paymentService).confirm(anyLong(), anyString(), anyString(), anyInt());

        mockMvc.perform(post("/reservations/" + 1 + "/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isInternalServerError());
    }
}
