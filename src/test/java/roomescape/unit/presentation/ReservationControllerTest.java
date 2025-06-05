package roomescape.unit.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.AuthorizationExtractor;
import roomescape.domain.PaymentInfo;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.MyReservationsResponse;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationWithPaymentResponse;
import roomescape.infrastructure.JwtTokenProvider;
import roomescape.presentation.ReservationController;
import roomescape.service.PaymentClient;
import roomescape.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {ReservationController.class, AuthorizationExtractor.class})
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentClient paymentClient;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @Test
    void 사용자가_예약을_생성한다() throws Exception {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(LocalDate.of(2025, 1, 1), 1L, 1L, "1", "normal", "1", 1000);
        ReservationWithPaymentResponse response =
                new ReservationWithPaymentResponse(
                        1L,
                        "memberName1",
                        LocalDate.of(2025, 1, 1),
                        new ReservationTimeResponse(1L, LocalTime.of(9, 0)),
                        "themeName1",
                        "paymentKey1",
                        1000
                );


        PaymentInfo paymentInfo = new PaymentInfo("paymentKey1", 1000, "orderId");
        given(paymentClient.postPaymentInfo(any())).willReturn(paymentInfo);
        given(reservationService.createReservationForMember(1L, request)).willReturn(response);
        given(tokenProvider.extractSubject("accessToken")).willReturn("1");
        // when & then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .cookie(new Cookie("token", "accessToken")))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 사용자가_예약을_조회한다() throws Exception {
        // given
        MyReservationsResponse response = new MyReservationsResponse(1L,
                "memberName1",
                LocalDate.of(2025, 1, 1),
                new ReservationTimeResponse(1L, LocalTime.of(9, 0)),
                "themeName1",
                "예약",
                "paymentKey1",
                1000);

        given(reservationService.findBookingHistory(1L)).willReturn(List.of(response));
        given(tokenProvider.extractSubject("accessToken")).willReturn("1");
        // when & then

        mockMvc.perform(get("/api/reservations/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "accessToken")))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(response))));
    }

    @Test
    void 예약을_삭제하는데_성공한다() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/reservations/{reservationId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
