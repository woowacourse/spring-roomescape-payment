package roomescape.reservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.BaseTest;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class ReservationControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Member member;
    private Theme theme;
    private ReservationTime time;
    private String token;
    private Payment payment;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.withDefaultRole("홍길동", "hong@example.com", "password"));
        theme = themeRepository.save(Theme.of("테마명", "테마 설명", "thumbnail.jpg"));
        time = reservationTimeRepository.save(ReservationTime.from(LocalTime.of(13, 0)));
        token = jwtTokenProvider.createToken(Jwts.claims().subject(member.getId().toString()).build());
        payment = paymentRepository.save(Payment.from("test_order_id_1", "test_payment_key_1", 1000L));
    }

    @Test
    void 예약_생성_성공() throws Exception {
        // given
        Map<String, String> params = new HashMap<>();
        LocalDate date = LocalDate.now().plusDays(1);
        params.put("date", date.toString());
        params.put("timeId", "1");
        params.put("themeId", "1");
        params.put("paymentKey", payment.getPaymentKey());
        params.put("orderId", payment.getOrderId());
        params.put("amount", payment.getAmount().toString());
        String content = new ObjectMapper().writeValueAsString(params);

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", token))
                        .content(content))
                .andExpect(status().isCreated());
    }

    @Test
    void 예약_목록_조회_성공() throws Exception {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now();
        Reservation reservation = Reservation.of(
                LocalDate.now().plusDays(1),
                time,
                theme,
                member,
                currentDateTime,
                payment
        );
        reservationRepository.save(reservation);

        // when & then
        mockMvc.perform(get("/reservations")
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isOk());
    }

    @Test
    void 예약_취소_성공() throws Exception {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now();
        Reservation reservation = Reservation.of(
                LocalDate.now().plusDays(1),
                time,
                theme,
                member,
                currentDateTime,
                payment
        );
        Reservation savedReservation = reservationRepository.save(reservation);

        // when & then
        mockMvc.perform(delete(String.format("/reservations/%d", savedReservation.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent());
    }

    @Test
    void 대기_목록_조회_성공() throws Exception {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now();
        Reservation waitingReservation = Reservation.waiting(
                LocalDate.now().plusDays(1),
                time,
                theme,
                member,
                currentDateTime,
                1L
        );
        reservationRepository.save(waitingReservation);

        // when & then
        mockMvc.perform(get("/reservations")
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isOk());
    }

    @Test
    void 예약_시간_검증_실패() throws Exception {
        // given
        Map<String, String> params = new HashMap<>();
        LocalDate date = LocalDate.now().minusDays(1);
        params.put("date", date.toString());
        params.put("timeId", "1");
        params.put("themeId", "1");
        params.put("orderId", payment.getOrderId());
        params.put("paymentKey", payment.getPaymentKey());
        params.put("amount", payment.getAmount().toString());
        String content = new ObjectMapper().writeValueAsString(params);

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", token))
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 인증_실패() throws Exception {
        // given
        Map<String, String> params = new HashMap<>();
        LocalDate date = LocalDate.now().plusDays(1);
        params.put("date", date.toString());
        params.put("timeId", "1");
        params.put("themeId", "1");
        params.put("orderId", payment.getOrderId());
        params.put("paymentKey", payment.getPaymentKey());
        params.put("amount", payment.getAmount().toString());
        String content = new ObjectMapper().writeValueAsString(params);

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }
}
