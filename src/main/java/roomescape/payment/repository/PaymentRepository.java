package roomescape.payment.repository;

import java.util.List;
import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findAll();

    Payment saveAndFlush(Payment payment);
}
