package roomescape.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.BaseRepositoryTest;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.PaymentFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Member member;

    private Theme theme;

    private ReservationTime time;

    @BeforeEach
    void setUp() {
        member = save(MemberFixture.prin());
        theme = save(ThemeFixture.theme());
        time = save(ReservationTimeFixture.ten());
    }

    @Test
    @DisplayName("예약에 해당하는 모든 결제를 조회한다")
    void findAllByReservationId() {
        List<Reservation> reservations = List.of(
                save(ReservationFixture.create("2024-06-09", member, time, theme)),
                save(ReservationFixture.create("2024-06-10", member, time, theme)),
                save(ReservationFixture.create("2024-06-11", member, time, theme)),
                save(ReservationFixture.create("2024-06-12", member, time, theme)),
                save(ReservationFixture.create("2024-06-13", member, time, theme))
        );
        for (Reservation reservation : reservations) {
            save(PaymentFixture.create(reservation));
        }

        List<Payment> result = paymentRepository.findAllByReservationIn(reservations);

        assertThat(result).hasSize(5);
    }
}
