package roomescape.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.util.CookieHandler;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.member.MemberNameResponseDto;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.dto.reservation.MemberReservationCreateRequestDto;
import roomescape.dto.reservation.MyReservationResponseDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.dto.theme.ThemeResponseDto;
import roomescape.dto.time.ReservationTimeResponseDto;
import roomescape.service.command.PaymentCommandService;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.dto.PaymentConfirmDto;
import roomescape.service.dto.ReservationCreateDto;
import roomescape.service.query.MemberQueryService;
import roomescape.service.query.ReservationQueryService;
import roomescape.util.JwtTokenProvider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @MockitoBean
    private PaymentCommandService paymentCommandService;

    @MockitoBean
    private CookieHandler cookieHandler;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private MemberQueryService memberQueryService;

    @MockitoBean
    private ReservationQueryService reservationQueryService;

    @MockitoBean
    private ReservationCommandService reservationCommandService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    String loginToken;

    Member member;

    Cookie cookie;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "가이온", "hello@woowa.com", Role.ADMIN, "password");
        loginToken = jwtTokenProvider.createToken(member);
        cookie = new Cookie("token", loginToken);
        when(memberQueryService.findMemberById(any(Long.class))).thenReturn(member);
    }

    @DisplayName("Reservation 목록 내용 갯수를 검사한다")
    @Test
    void reservationFindAllTest() throws Exception {
        when(reservationQueryService.findAllReservations()).thenReturn(
                List.of(new ReservationResponseDto(
                        1L, null, null, null, null, null
                ))
        );
        mockMvc.perform(get("/reservations")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @DisplayName("자신의 예약 정보를 불러올 수 있다")
    @Test
    void myReservationTest() throws Exception {
        when(reservationQueryService.findMyReservations(any(LoginInfo.class))).thenReturn(
                List.of(new MyReservationResponseDto(
                        1L, null, null, null, null
                ))
        );
        mockMvc.perform(get("/reservations/me")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @DisplayName("Reservation을 생성한다")
    @Test
    void addReservationTest() throws Exception {
        MemberReservationCreateRequestDto requestDto = new MemberReservationCreateRequestDto(
                LocalDate.of(2025, 8, 5),
                1L, 1L, "paymentKey", "orderId", 1000L);
        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(requestDto.date(), requestDto.timeId(), requestDto.themeId(), 1L);
        ReservationResponseDto reservationResponseDto = new ReservationResponseDto(
                1L,
                new MemberNameResponseDto("test"),
                requestDto.date(),
                new ThemeResponseDto(1L, "name", "description", "thumbnail"),
                new ReservationTimeResponseDto(1L, LocalTime.of(12, 0)),
                "예약");
        when(reservationCommandService.bookReservation(reservationCreateDto))
                .thenReturn(reservationResponseDto);
        when(paymentCommandService.confirmPayment(new PaymentConfirmDto(requestDto.paymentKey(), requestDto.orderId(), requestDto.amount())))
                .thenReturn(new PaymentResponseDto(requestDto.paymentKey(), requestDto.orderId(), requestDto.amount()));

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(paymentCommandService, atLeastOnce()).confirmPayment(requestDto.extractTossPaymentDto());
    }
}
