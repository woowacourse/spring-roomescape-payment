package roomescape.domain.payment;

import java.util.List;

public interface PaymentRepository {

    void save(Payment payment);

    List<Payment> findByReservationIdIn(List<Long> reservationIds);
}
