package roomescape.approval.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.approval.domain.Approval;
import roomescape.reservation.domain.Reservation;

public interface ApprovalJpaRepository extends JpaRepository<Approval, Long> {
    void deleteByReservation(Reservation reservation);
}
