package roomescape.payment.model.repository;

import java.util.List;
import roomescape.payment.model.entity.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findAllByReservationIdIn(List<Long> reservationIds);
}
