package roomescape.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.domain.PaymentInfo;
import roomescape.dto.response.MemberResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ThemeResponse;
import roomescape.dto.response.TimeSlotResponse;
import roomescape.infrastructure.CheckAuthenticationInterceptor;
import roomescape.infrastructure.LoginMemberArgumentResolver;
import roomescape.infrastructure.TossPaymentClient;
import roomescape.service.ReservationService;

@WebMvcTest(ReservationController.class)
public class MockReservationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TossPaymentClient paymentClient;
    @MockBean
    private ReservationService reservationService;
    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;
    @MockBean
    private CheckAuthenticationInterceptor checkAuthenticationInterceptor;

    @DisplayName("reservation 페이지에 새로운 예약 정보를 추가할 수 있다.")
    @Test
    void given_when_saveAndDeleteReservations_then_statusCodeIsOkay() throws Exception {
        PaymentInfo paymentInfo = new PaymentInfo(BigDecimal.valueOf(1000000000), "orderId", "paymentKey");
        MemberResponse memberResponse = new MemberResponse(1L, "atto");
        TimeSlotResponse timeSlotResponse = new TimeSlotResponse(1L, LocalTime.of(3, 20));
        ThemeResponse themeResponse = new ThemeResponse(1L, "ash", "description", "thumbnail", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(1L, memberResponse,
                LocalDate.of(2999, 12, 31), timeSlotResponse, themeResponse);

        given(reservationService.checkAvailableReservation(any(), any())).willReturn(null);
        given(paymentClient.payment(any())).willReturn(paymentInfo);
        given(reservationService.confirmReservationByClient(any())).willReturn(reservationResponse);

        String jsonRequest = """
                 {
                    "date": "2999-12-31",
                    "timeId": 1,
                    "themeId": 1,
                    "paymentKey": "paymentKey",
                    "orderId": "orderId",
                    "amount": 1000000000
                  }
                """;

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @DisplayName("금액이 10억이 넘으면 결제할 수 없다.")
    @Test
    void given_moreThanBillionWon_when_saveReservations_then_statusCodeIs() throws Exception {
        PaymentInfo paymentInfo = new PaymentInfo(BigDecimal.valueOf(1000000001), "orderId", "paymentKey");
        MemberResponse memberResponse = new MemberResponse(1L, "atto");
        TimeSlotResponse timeSlotResponse = new TimeSlotResponse(1L, LocalTime.of(3, 20));
        ThemeResponse themeResponse = new ThemeResponse(1L, "ash", "description", "thumbnail", BigDecimal.valueOf(10000));
        ReservationResponse reservationResponse = new ReservationResponse(1L, memberResponse,
                LocalDate.of(2999, 12, 31), timeSlotResponse, themeResponse);

        given(reservationService.checkAvailableReservation(any(), any())).willReturn(null);
        given(paymentClient.payment(any())).willReturn(paymentInfo);
        given(reservationService.confirmReservationByClient(any())).willReturn(reservationResponse);

        String jsonRequest = """
                 {
                    "date": "2999-12-31",
                    "timeId": 1,
                    "themeId": 1,
                    "paymentKey": "paymentKey",
                    "orderId": "orderId",
                    "amount": 1000000001
                  }
                """;

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
