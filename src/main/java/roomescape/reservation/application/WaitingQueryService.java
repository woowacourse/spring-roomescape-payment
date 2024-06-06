package roomescape.reservation.application;

import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.WaitingReservation;

import java.util.List;

@Service
public class WaitingQueryService {
    private final ReservationRepository reservationRepository;

    public WaitingQueryService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<WaitingReservation> findAllWithPreviousCountByMember(Member member) {
        return reservationRepository.findWaitingReservationsByMemberWithDetails(member);
    }

    public List<Reservation> findAllUnpaidByMember(Member member) {
        return reservationRepository.findAllByMemberAndStatus(member, ReservationStatus.PENDING_PAYMENT);
    }

    public List<Reservation> findAll() {
        List<ReservationStatus> statusConditions = List.of(ReservationStatus.WAITING, ReservationStatus.PENDING_PAYMENT);
        return reservationRepository.findAllByStatusWithDetails(statusConditions);
    }
}
