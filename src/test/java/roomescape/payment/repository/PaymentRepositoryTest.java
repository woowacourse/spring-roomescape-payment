package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.TODAY;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentMethod;
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
    private ReservationRepository reservationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void findByReservationId() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        Member kaki = memberRepository.save(KAKI);
        Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

        paymentRepository.save(new Payment(reservation, "test123", "order123", "DONE", PaymentMethod.EASY_PAY, 1000));
        Payment payment = paymentRepository.findByReservationId(reservation.getId()).get();

        assertThat(payment.getReservation().getId()).isEqualTo(reservation.getId());
    }
}
