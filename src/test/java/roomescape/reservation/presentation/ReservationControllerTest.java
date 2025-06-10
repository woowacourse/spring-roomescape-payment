package roomescape.reservation.presentation;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.client.TossPaymentTestConfig;
import roomescape.common.argumentResolver.LoginArgumentResolver;
import roomescape.common.config.ReservationConfig;
import roomescape.common.util.JwtTokenContainer;
import roomescape.common.util.TokenCookieManager;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.service.LoginService;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.MyReservationWithPaymentResponse;
import roomescape.reservation.dto.response.ReservationWithPaymentResponse;
import roomescape.reservation.service.ReservationPaymentFacade;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.theme.dto.response.ThemeResponse;

import org.junit.jupiter.api.Test;

@WebMvcTest(ReservationController.class)
@Import({TossPaymentTestConfig.class, ReservationConfig.class, LoginArgumentResolver.class})
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
    private LoginService loginService;

    @MockitoBean
    private TokenCookieManager tokenCookieManager;

    @MockitoBean
    private JwtTokenContainer jwtTokenContainer;

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

    @Test
    void 본인_예약_목록을_조회한다() throws Exception {
        // given
        LoginMember loginMember = new LoginMember(1L, "포라");
        List<MyReservationWithPaymentResponse> expectedResponses = List.of(
                new MyReservationWithPaymentResponse(1L, "theme1", "2024-03-20", "10:00", "예약", "paymentKey1", "1000"),
                new MyReservationWithPaymentResponse(2L, "theme2", "2024-03-21", "11:00", "예약", "paymentKey2", "2000")
        );

        when(loginService.loginCheck(any()))
                .thenReturn(loginMember);
        when(reservationService.getMyReservations(1L))
                .thenReturn(expectedResponses);

        // when & then
        mockMvc.perform(get("/reservations/mine")
                        .cookie(new jakarta.servlet.http.Cookie("token", "test-token")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].theme").value("theme1"))
                .andExpect(jsonPath("$[0].date").value("2024-03-20"))
                .andExpect(jsonPath("$[0].time").value("10:00"))
                .andExpect(jsonPath("$[0].status").value("예약"))
                .andExpect(jsonPath("$[0].paymentKey").value("paymentKey1"))
                .andExpect(jsonPath("$[0].amount").value("1000"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].theme").value("theme2"))
                .andExpect(jsonPath("$[1].date").value("2024-03-21"))
                .andExpect(jsonPath("$[1].time").value("11:00"))
                .andExpect(jsonPath("$[1].status").value("예약"))
                .andExpect(jsonPath("$[1].paymentKey").value("paymentKey2"))
                .andExpect(jsonPath("$[1].amount").value("2000"));
    }

}
