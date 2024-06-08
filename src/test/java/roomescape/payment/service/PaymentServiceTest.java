package roomescape.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.assertj.core.api.Assertions;
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
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.dto.response.ReservationPaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;

@SpringBootTest
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private CanceledPaymentRepository canceledPaymentRepository;

    @Test
    @DisplayName("결제 정보를 저장한다.")
    void savePayment() {
        // given
        PaymentResponse paymentInfo = new PaymentResponse("payment-key", "order-id", OffsetDateTime.now(), 10000L);
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1L);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Member member = memberRepository.save(new Member("member", "email@email.com", "password", Role.MEMBER));
        Theme theme = themeRepository.save(new Theme("name", "desc", "thumbnail"));
        Reservation reservation = reservationRepository.save(new Reservation(date, time, theme, member,
                ReservationStatus.CONFIRMED));

        // when
        ReservationPaymentResponse reservationPaymentResponse = paymentService.savePayment(paymentInfo, reservation);

        // then
        assertThat(reservationPaymentResponse.reservation().id()).isEqualTo(reservation.getId());
        assertThat(reservationPaymentResponse.paymentKey()).isEqualTo(paymentInfo.paymentKey());
    }

    @Test
    @DisplayName("예약 ID로 결제 정보를 제거하고, 결제 취소 테이블에 취소 정보를 저장한다.")
    void cancelPaymentByAdmin() {
        // given
        PaymentResponse paymentInfo = new PaymentResponse("payment-key", "order-id", OffsetDateTime.now(), 10000L);
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1L);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Member member = memberRepository.save(new Member("member", "email@email.com", "password", Role.MEMBER));
        Theme theme = themeRepository.save(new Theme("name", "desc", "thumbnail"));
        Reservation reservation = reservationRepository.save(new Reservation(date, time, theme, member,
                ReservationStatus.CONFIRMED));

        paymentService.savePayment(paymentInfo, reservation);

        // when
        PaymentCancelRequest paymentCancelRequest = paymentService.cancelPaymentByAdmin(reservation.getId());

        // then
        assertThat(canceledPaymentRepository.findByPaymentKey("payment-key")).isNotEmpty();
        assertThat(paymentCancelRequest.paymentKey()).isEqualTo(paymentInfo.paymentKey());
        assertThat(paymentCancelRequest.cancelReason()).isEqualTo("고객 요청");
        assertThat(paymentCancelRequest.amount()).isEqualTo(10000L);
    }

    @DisplayName("결제 취소 정보에 있는 취소 시간을 업데이트한다.")
    @Test
    void updateCanceledTime() {
        // given
        PaymentResponse paymentInfo = new PaymentResponse("payment-key", "order-id", OffsetDateTime.now(), 10000L);
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1L);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Member member = memberRepository.save(new Member("member", "email@email.com", "password", Role.MEMBER));
        Theme theme = themeRepository.save(new Theme("name", "desc", "thumbnail"));
        Reservation reservation = reservationRepository.save(new Reservation(date, time, theme, member,
                ReservationStatus.CONFIRMED));

        paymentService.savePayment(paymentInfo, reservation);
        paymentService.cancelPaymentByAdmin(reservation.getId());

        // when
        OffsetDateTime canceledAt = OffsetDateTime.now().plusHours(2L);
        paymentService.updateCanceledTime(paymentInfo.paymentKey(), canceledAt);

        // then
        canceledPaymentRepository.findByPaymentKey(paymentInfo.paymentKey())
                .ifPresent(canceledPayment -> assertThat(canceledPayment.getCanceledAt()).isEqualTo(canceledAt));
    }
}
