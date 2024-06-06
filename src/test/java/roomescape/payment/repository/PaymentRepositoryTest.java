package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.ORDER_ID;
import static roomescape.util.Fixture.PAYMENT_KEY;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.TODAY;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("결제 키로 결제 데이터를 조회한다.")
    @Test
    void findByPaymentKey() {
        Member kaki = memberRepository.save(KAKI);
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horroTheme = themeRepository.save(HORROR_THEME);
        Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horroTheme, hour10, ReservationStatus.SUCCESS));
        Payment payment = paymentRepository.save(new Payment(reservation, PAYMENT_KEY, ORDER_ID, PaymentStatus.DONE, PaymentMethod.EASY_PAY, 1000));

        Payment findPayment = paymentRepository.findByPaymentKey(payment.getPaymentKey()).get();

        assertThat(payment.getId()).isEqualTo(findPayment.getId());
    }

    @DisplayName("예약 아이디로 결제 데이터를 조회한다.")
    @Test
    void findByReservationId() {
        Member kaki = memberRepository.save(KAKI);
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horroTheme = themeRepository.save(HORROR_THEME);
        Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horroTheme, hour10, ReservationStatus.SUCCESS));
        Payment payment = paymentRepository.save(new Payment(reservation, PAYMENT_KEY, ORDER_ID, PaymentStatus.DONE, PaymentMethod.EASY_PAY, 1000));

        Payment findPayment = paymentRepository.findByReservationId(reservation.getId()).get();

        assertThat(payment.getId()).isEqualTo(findPayment.getId());
    }
}
