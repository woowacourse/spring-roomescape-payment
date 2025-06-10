package roomescape.payment.repository;

import java.util.List;
import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(final Payment payment);
    List<Payment> findByReservationIdIn(final List<Long> reservationIds);
}
