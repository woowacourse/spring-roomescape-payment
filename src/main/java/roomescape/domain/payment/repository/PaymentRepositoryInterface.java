package roomescape.domain.payment.repository;

import java.util.List;
import roomescape.domain.payment.entity.Payment;

public interface PaymentRepositoryInterface {

    Payment save(final Payment payment);

    void deleteByReservationId(final Long reservationId);

    List<Payment> findAll();
}
