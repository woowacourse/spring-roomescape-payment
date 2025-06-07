package roomescape.approval.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.approval.domain.Approval;
import roomescape.reservation.domain.Reservation;

public interface ApprovalJpaRepository extends JpaRepository<Approval, Long> {
    void deleteByReservation(Reservation reservation);

    @Query("SELECT a FROM Approval a WHERE a.reservation IN :reservations")
    List<Approval> findAllByReservationsIn(List<Reservation> reservations);
}
