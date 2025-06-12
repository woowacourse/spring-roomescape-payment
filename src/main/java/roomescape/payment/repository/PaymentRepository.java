package roomescape.payment.repository;

import java.util.List;
import java.util.Optional;
import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByPaymentKey(String s);

    Optional<Payment> findByReservationId(Long id);

    List<Payment> findAllByReservationIds(List<Long> reservationIds);

    void deleteById(Long id);
}
