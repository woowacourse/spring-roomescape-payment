package roomescape.reservation.presentation;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.client.TossPaymentTestConfig;
import roomescape.common.config.ReservationConfig;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.response.MemberResponse;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationWithPaymentResponse;
import roomescape.reservation.service.ReservationPaymentFacade;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.theme.dto.response.ThemeResponse;

import org.junit.jupiter.api.Test;

@WebMvcTest(ReservationController.class)
@Import(TossPaymentTestConfig.class)
class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private ReservationPaymentFacade reservationPaymentFacade;

    @MockitoBean
    private ReservationConfig reservationConfig;

    @Test
    void 결제_승인에_성공한다() throws Exception {
        // given
        ReservationWithPaymentRequest reservationWithPaymentRequest = new ReservationWithPaymentRequest(
                LocalDate.now(),
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000L
        );
        LoginMember loginMember = new LoginMember(1L, "포라");

        ReservationWithPaymentResponse response = new ReservationWithPaymentResponse(
                1L,
                new MemberResponse(1L, "포라"),
                LocalDate.now(),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "theme1", "des1", "thum1"),
                1L
        );

        // when
        when(reservationPaymentFacade.createReservationAndSavePayment(any(), any()))
                .thenReturn(response);
        String jsonContent = objectMapper.writeValueAsString(reservationWithPaymentRequest);

        // then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .requestAttr("loginMember", loginMember))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }
}
