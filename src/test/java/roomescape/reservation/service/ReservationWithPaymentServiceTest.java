package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.domain.repository.CanceledPaymentRepository;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;

@SpringBootTest
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationWithPaymentServiceTest {

    @Autowired
    private ReservationWithPaymentService reservationWithPaymentService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CanceledPaymentRepository canceledPaymentRepository;

    @Test
    @DisplayName("예약과 결제 정보를 추가한다.")
    void addReservationWithPayment() {
        // given
        PaymentResponse paymentInfo = new PaymentResponse("payment-key", "order-id", OffsetDateTime.now(), 10000L);
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1L).withNano(0);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Member member = memberRepository.save(new Member("member", "email@email.com", "password", Role.MEMBER));
        Theme theme = themeRepository.save(new Theme("name", "desc", "thumbnail"));
        ReservationRequest reservationRequest = new ReservationRequest(date, time.getId(), theme.getId(), "payment-key",
                "order-id", 10000L, "NORMAL");

        // when
        ReservationResponse reservationResponse = reservationWithPaymentService.addReservationWithPayment(
                reservationRequest, paymentInfo, member.getId());

        // then
        reservationRepository.findById(reservationResponse.id())
                .ifPresent(reservation -> {
                    assertThat(reservation.getMember().getId()).isEqualTo(member.getId());
                    assertThat(reservation.getTheme().getId()).isEqualTo(theme.getId());
                    assertThat(reservation.getDate()).isEqualTo(date);
                    assertThat(reservation.getReservationTime().getId()).isEqualTo(time.getId());
                    assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.CONFIRMED);
                });
        paymentRepository.findByPaymentKey("payment-key")
                .ifPresent(payment -> {
                    assertThat(payment.getReservation().getId()).isEqualTo(reservationResponse.id());
                    assertThat(payment.getPaymentKey()).isEqualTo("payment-key");
                    assertThat(payment.getOrderId()).isEqualTo("order-id");
                    assertThat(payment.getTotalAmount()).isEqualTo(10000L);
                });
    }

    @Test
    @DisplayName("예약 ID를 이용하여 예약과 결제 정보를 제거하고, 결제 취소 정보를 저장한다.")
    void removeReservationWithPayment() {
        // given
        PaymentResponse paymentInfo = new PaymentResponse("payment-key", "order-id", OffsetDateTime.now(), 10000L);
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1L).withNano(0);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Member member = memberRepository.save(new Member("member", "admin@email.com", "password", Role.ADMIN));
        Theme theme = themeRepository.save(new Theme("name", "desc", "thumbnail"));
        ReservationRequest reservationRequest = new ReservationRequest(date, time.getId(), theme.getId(), "payment-key",
                "order-id", 10000L, "NORMAL");

        ReservationResponse reservationResponse = reservationWithPaymentService.addReservationWithPayment(
                reservationRequest, paymentInfo, member.getId());

        // when
        PaymentCancelRequest paymentCancelRequest = reservationWithPaymentService.removeReservationWithPayment(
                reservationResponse.id(), member.getId());

        // then
        assertThat(paymentCancelRequest.cancelReason()).isEqualTo("고객 요청");
        assertThat(reservationRepository.findById(reservationResponse.id())).isEmpty();
        assertThat(paymentRepository.findByPaymentKey("payment-key")).isEmpty();
        assertThat(canceledPaymentRepository.findByPaymentKey("payment-key")).isNotEmpty();
    }

    @Test
    @DisplayName("결제 정보가 없으면 True를 반환한다.")
    void isNotPaidReservation() {
        // given
        PaymentResponse paymentInfo = new PaymentResponse("payment-key", "order-id", OffsetDateTime.now(), 10000L);
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1L);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Member member = memberRepository.save(new Member("member", "admin@email.com", "password", Role.ADMIN));
        Theme theme = themeRepository.save(new Theme("name", "desc", "thumbnail"));

        Reservation saved = reservationRepository.save(
                new Reservation(date, time, theme, member, ReservationStatus.CONFIRMED_PAYMENT_REQUIRED));

        // when
        boolean result = reservationWithPaymentService.isNotPaidReservation(saved.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("결제 정보가 있으면 False를 반환한다.")
    void isPaidReservation() {
        // given
        PaymentResponse paymentInfo = new PaymentResponse("payment-key", "order-id", OffsetDateTime.now(), 10000L);
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1L).withNano(0);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Member member = memberRepository.save(new Member("member", "admin@email.com", "password", Role.ADMIN));
        Theme theme = themeRepository.save(new Theme("name", "desc", "thumbnail"));
        ReservationRequest reservationRequest = new ReservationRequest(date, time.getId(), theme.getId(), "payment-key",
                "order-id", 10000L, "NORMAL");

        ReservationResponse reservationResponse = reservationWithPaymentService.addReservationWithPayment(
                reservationRequest, paymentInfo, member.getId());

        // when
        boolean result = reservationWithPaymentService.isNotPaidReservation(reservationResponse.id());

        // then
        assertThat(result).isFalse();
    }
}
