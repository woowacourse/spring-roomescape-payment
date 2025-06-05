package roomescape.domain.payment;

import java.util.Optional;

public interface PaymentRepository {

    void save(Payment payment);

    boolean existsPaymentByReservationId(Long reservationId);

    Optional<Payment> findPaymentByReservationId(Long reservationId);
}
