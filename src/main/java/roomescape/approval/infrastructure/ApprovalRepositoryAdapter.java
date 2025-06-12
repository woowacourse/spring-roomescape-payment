package roomescape.approval.infrastructure;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.approval.domain.Approval;
import roomescape.approval.domain.repository.ApprovalRepository;
import roomescape.reservation.domain.Reservation;

@Repository
@AllArgsConstructor
public class ApprovalRepositoryAdapter implements ApprovalRepository {
    private final ApprovalJpaRepository approvalJpaRepository;

    @Override
    public Approval save(Approval approval) {
        return approvalJpaRepository.save(approval);
    }

    @Override
    public void deleteByReservation(Reservation reservation) {
        approvalJpaRepository.deleteByReservation(reservation);
    }

    @Override
    public List<Approval> findAllByReservationsIn(List<Reservation> reservations) {
        return approvalJpaRepository.findAllByReservationsIn(reservations);
    }
}
