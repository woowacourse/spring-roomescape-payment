package roomescape.integration.fixture;

import org.springframework.stereotype.Component;
import roomescape.member.domain.Member;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.wait.domain.ReservationWait;
import roomescape.wait.repository.ReservationWaitRepository;

@Component
public class ReservationWaitDbFixture {
    private ReservationWaitRepository reservationWaitRepository;

    public ReservationWaitDbFixture(final ReservationWaitRepository reservationWaitRepository) {
        this.reservationWaitRepository = reservationWaitRepository;
    }

    public ReservationWait createReservationWait(
            final ReservationSchedule schedule,
            final Member member
    ) {
        return reservationWaitRepository.save(new ReservationWait(null, member, schedule));
    }
}
