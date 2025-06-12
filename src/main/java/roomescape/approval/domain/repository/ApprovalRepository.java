package roomescape.approval.domain.repository;

import java.util.List;
import roomescape.approval.domain.Approval;
import roomescape.reservation.domain.Reservation;

public interface ApprovalRepository {
    Approval save(Approval approval);

    void deleteByReservation(Reservation reservation);

    List<Approval> findAllByReservationsIn(List<Reservation> reservations);
}
