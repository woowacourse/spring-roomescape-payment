package roomescape.payment.domain.repository;

import roomescape.payment.domain.Payment;
import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    List<Payment> findAllByMemberId(Long memberId);
}
