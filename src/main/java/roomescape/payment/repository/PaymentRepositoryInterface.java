package roomescape.payment.repository;

import java.util.List;
import roomescape.payment.entity.Payment;

public interface PaymentRepositoryInterface {

    Payment save(final Payment payment);

    List<Payment> findAll();

    void deleteByReservationId(final Long reservationId);
}
