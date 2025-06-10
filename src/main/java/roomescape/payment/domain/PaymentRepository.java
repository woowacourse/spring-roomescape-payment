package roomescape.payment.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findByReservationId(Long reservationId);

    Payment save(Payment payment);

    void deleteByReservationId(Long id);
}
