package roomescape.domain.reservation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.exception.reservation.DuplicatedReservationException;

@Component
@RequiredArgsConstructor
public class ReservationFactory {
    private final ReservationRepository reservationRepository;

    public Reservation createReservation(ReservationDetail detail, Member member) {
        detail.validatePastTime();
        rejectDuplicateReservation(detail, member);
        return getReservation(detail, member);
    }

    private void rejectDuplicateReservation(ReservationDetail detail, Member member) {
        if (reservationRepository.existsReservation(detail, member, Status.getStatusWithoutCancel())) {
            throw new DuplicatedReservationException();
        }
    }

    private Reservation getReservation(ReservationDetail detail, Member member) {
        if (reservationRepository.existsReservation(detail, List.of(Status.RESERVED, Status.PAYMENT_PENDING))) {
            return new Reservation(member, detail, Status.WAITING);
        }
        return new Reservation(member, detail, Status.RESERVED);
    }
}
