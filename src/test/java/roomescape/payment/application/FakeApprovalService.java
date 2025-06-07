package roomescape.payment.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import roomescape.approval.application.ApprovalService;
import roomescape.approval.domain.Approval;
import roomescape.approval.domain.Onsite;
import roomescape.reservation.domain.Reservation;

public class FakeApprovalService extends ApprovalService {
    public FakeApprovalService() {
        super(null, null);
    }

    @Override
    public void approve(Approval approval) {
    }

    @Override
    public void deleteByReservation(Optional<Reservation> reservation) {
    }

    @Override
    public List<Approval> findAllByReservationIn(List<Reservation> reservations) {
        List<Approval> approvals = new ArrayList<>();
        for (Reservation reservation : reservations) {
            approvals.add(new Onsite(reservation, BigDecimal.valueOf(10000)));
        }
        return approvals;
    }
}
