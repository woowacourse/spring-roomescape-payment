package roomescape.reservation.repository.payment;

import roomescape.reservation.domain.CompletedPayment;

public interface CompletedPaymentRepository {
    CompletedPayment save(CompletedPayment completedPayment);

    boolean existsByReservationId(Long reservationId);
}
