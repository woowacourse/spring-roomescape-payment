package roomescape.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.common.RepositoryTest;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationTimeRepository;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;

class PaymentRepositoryTest extends RepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("예약 id로 결제 내역을 조회한다.")
    void findByReservationId() {
        // given
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(MIA_RESERVATION_TIME));
        Theme theme = themeRepository.save(WOOTECO_THEME());
        Member member = memberRepository.save(USER_MIA());
        Reservation reservation = reservationRepository.save(MIA_RESERVATION(time, theme, member, BOOKING));

        Payment savedPayment = paymentRepository.save(new Payment("paymentKey", "orderId", 10L, reservation));

        // when
        Optional<Payment> payment = paymentRepository.findByReservationId(reservation.getId());

        // then
        assertThat(payment.get()).isEqualTo(savedPayment);
    }
}
