package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.domain.member.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.*;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("예약, 결제 상태로 결제 정보를 조회한다.")
    void findByReservationAndStatus() {
        // given
        final Member member = MEMBER_TENNY();
        final ReservationTime reservationTime = RESERVATION_TIME_SIX();
        final Theme theme = THEME_HORROR();
        final Reservation reservation = new Reservation(member, DATE_MAY_EIGHTH, reservationTime, theme, ReservationStatus.RESERVED);
        final Payment payment = new Payment(reservation, PAYMENT_KEY, ORDER_ID, AMOUNT, PaymentStatus.PAID);
        testEntityManager.persist(member);
        testEntityManager.persist(reservationTime);
        testEntityManager.persist(theme);
        testEntityManager.flush();
        testEntityManager.persist(reservation);
        testEntityManager.flush();
        testEntityManager.persist(payment);
        testEntityManager.flush();

        // when
        final Optional<Payment> actual = paymentRepository.findByReservationAndStatus(reservation, PaymentStatus.PAID);

        // then
        assertThat(actual).isNotEmpty();
    }
}
