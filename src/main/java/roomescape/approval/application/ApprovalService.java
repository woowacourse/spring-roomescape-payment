package roomescape.approval.application;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.approval.application.command.ApprovalCommandService;
import roomescape.approval.application.command.ApprovalCommandServiceProvider;
import roomescape.approval.domain.Approval;
import roomescape.approval.domain.repository.ApprovalRepository;
import roomescape.reservation.domain.Reservation;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ApprovalService {
    private final ApprovalCommandServiceProvider approvalCommandServiceProvider;
    private final ApprovalRepository approvalRepository;

    @Transactional
    public void approve(Approval approval) {
        ApprovalCommandService<Approval> approvalCommandService = approvalCommandServiceProvider.findService(approval);
        approvalCommandService.approve(approval);
    }

    @Transactional
    public void deleteByReservation(Reservation reservation) {
        approvalRepository.deleteByReservation(reservation);
    }

    public List<Approval> findAllByReservationIn(List<Reservation> reservations) {
        return approvalRepository.findAllByReservationsIn(reservations);
    }
}
