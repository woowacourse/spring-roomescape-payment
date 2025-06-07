package roomescape.approval.domain.repository;

import roomescape.approval.domain.Approval;
import roomescape.reservation.domain.Reservation;

public interface ApprovalRepository {
    Approval save(Approval approval);

    void deleteByReservation(Reservation reservation);
}
