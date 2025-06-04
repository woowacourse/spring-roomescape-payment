package roomescape.presentation.rest;

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
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.domain.user.UserRole;
import roomescape.exception.PaymentFailedException;
import roomescape.presentation.GlobalExceptionHandler;
import roomescape.presentation.StubAuthenticationInfoArgumentResolver;

public class PaymentControllerTest {

    private final long userId = 99L;

    private final PaymentService paymentService = Mockito.mock(PaymentService.class);
    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new PaymentController(paymentService))
        .setCustomArgumentResolvers(new StubAuthenticationInfoArgumentResolver(new AuthenticationInfo(userId, UserRole.USER)))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    @Test
    @DisplayName("예약에 대한 결제 승인 요청시, OK를 응답한다.")
    void payReservation() throws Exception {
        Mockito.doNothing().when(paymentService).pay(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "reservationId": "1",
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
            .when(paymentService).pay(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "reservationId": "1",
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
            .when(paymentService).pay(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "reservationId": "1",
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
            .when(paymentService).pay(anyString(), anyString(), anyLong());

        mockMvc.perform(post("/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "reservationId": "1",
                        "paymentKey": "a",
                        "orderId": "1",
                        "amount": 1000
                    }
                    """))
            .andExpect(status().isInternalServerError());
    }
}
