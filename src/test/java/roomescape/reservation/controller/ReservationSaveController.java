package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.common.config.ControllerTest;
import roomescape.common.util.CookieUtils;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.controller.dto.request.ReservationPaymentSaveRequest;

@AutoConfigureMockMvc
class ReservationSaveController extends ControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @DisplayName("회원이 예약을 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveMemberReservation() throws Exception {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        ReservationPaymentSaveRequest reservationPaymentSaveRequest = new ReservationPaymentSaveRequest(
                LocalDate.now(), 1L, 1L, "paymentKey", "orderId", 1000L
        );

        doNothing().when(paymentService).confirmPayment(any());

        mockMvc.perform(post("/reservations")
                        .cookie(new Cookie(CookieUtils.TOKEN_KEY, getMemberToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPaymentSaveRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/reservations/1"));
    }
}
