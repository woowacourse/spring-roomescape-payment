package roomescape.repository;

import java.util.List;
import java.util.Optional;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);

    List<Payment> findAllByReservationIn(List<Reservation> reservations);

    Optional<Payment> findByOrderIdAndPaymentKey(String orderId, String paymentKey);
}
