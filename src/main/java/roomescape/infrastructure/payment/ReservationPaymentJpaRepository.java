package roomescape.infrastructure.payment;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;

public interface ReservationPaymentJpaRepository extends
        ReservationPaymentRepository,
        ListCrudRepository<ReservationPayment, String> {

    Optional<ReservationPayment> findByOrderId(String orderId);

    @Override
    default ReservationPayment getByOrderId(String orderId) {
        return findByOrderId(orderId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제 정보입니다."));
    }
}
