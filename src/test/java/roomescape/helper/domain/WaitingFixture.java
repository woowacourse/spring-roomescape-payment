package roomescape.helper.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;

import java.util.List;

@Component
public class WaitingFixture {
    @Autowired
    private ReservationWaitingRepository reservationWaitingRepository;

    public ReservationWaiting createWaiting(Reservation reservation, Member member) {
        ReservationWaiting reservationWaiting = new ReservationWaiting(reservation, member);
        return reservationWaitingRepository.save(reservationWaiting);
    }

    public List<ReservationWaiting> findAllWaiting() {
        return reservationWaitingRepository.findAll();
    }

    public ReservationWaiting getById(Long id) {
        return reservationWaitingRepository.getReservationWaitingById(id);
    }
}
