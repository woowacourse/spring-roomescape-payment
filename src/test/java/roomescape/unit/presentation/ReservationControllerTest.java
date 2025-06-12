package roomescape.unit.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.AuthorizationExtractor;
import roomescape.auth.Role;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationWithStatusResponse;
import roomescape.infrastructure.JwtTokenProvider;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.presentation.ReservationController;
import roomescape.service.PaymentService;
import roomescape.service.ReservationFacade;
import roomescape.service.ReservationService;

@WebMvcTest(value = {ReservationController.class, AuthorizationExtractor.class})
class ReservationControllerTest {

    private static final Member member = Member.createWithoutId("이름", "email", "123", Role.MEMBER);
    private static final ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
    private static final Theme theme = Theme.createWithoutId("이름", "설명", "섬네일");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentClient paymentClient;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private ReservationFacade reservationFacade;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @Test
    void 사용자가_예약을_생성한다() throws Exception {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(LocalDate.of(2025, 1, 1), 1L, 1L, "1", "1",
                1000);
        ReservationResponse response = new ReservationResponse(1L, "memberName1", LocalDate.of(2025, 1, 1),
                new ReservationTimeResponse(1L, LocalTime.of(9, 0)), "themeName1");
        PaymentRequest paymentRequest = new PaymentRequest(1000, "1", "1");
        Reservation reservation = Reservation.createWithoutId(member, LocalDate.of(2026, 8, 8), time, theme);
        Payment payment = Payment.createPaymentWithoutId("10", reservation, "1", 1000);

        given(paymentService.approve(any())).willReturn(payment);
        given(reservationService.reserveWithPayment(1L, request.timeId(), request.themeId(),
                request.date(), payment)).willReturn(response);
        given(reservationFacade.processReservationForMember(1L, request.timeId(), request.themeId(), request.date(),
                paymentRequest)).willReturn(response);
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
        ReservationWithStatusResponse response = new ReservationWithStatusResponse(1L, "memberName1",
                LocalDate.of(2025, 1, 1),
                new ReservationTimeResponse(1L, LocalTime.of(9, 0)), "themeName1", "예약");

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
