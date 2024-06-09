package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    default Payment findByReservationIdOrThrow(Long reservationId) {
        return findByReservationId(reservationId).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.PAYMENT_NOT_FOUND_BY_RESERVATION_ID,
                "reservation_id = " + reservationId
        ));
    }

    Optional<Payment> findByReservationId(Long reservationId);

    boolean existsByReservationId(Long reservationId);
}
