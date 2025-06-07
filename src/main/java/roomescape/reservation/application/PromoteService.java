package roomescape.reservation.application;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.approval.application.ApprovalService;
import roomescape.approval.domain.OnSite;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSpec;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingRepository;
import roomescape.waiting.domain.Waitings;

@Service
@AllArgsConstructor
@Transactional
public class PromoteService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final ApprovalService approvalService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void promoteWaiting(Reservation reservation) {
        ReservationSpec spec = reservation.getSpec();
        Waitings waitings = new Waitings(waitingRepository.findBySpec(spec));
        Waiting waiting = waitings.pollHighestPriority();

        if (waiting == null) {
            return;
        }
        waitingRepository.deleteById(waiting.getId());

        Reservation newReservation = reservationRepository.save(new Reservation(waiting.getMember(), spec));
        approvalService.approve(new OnSite(newReservation, BigDecimal.valueOf(1000)));
    }
}
