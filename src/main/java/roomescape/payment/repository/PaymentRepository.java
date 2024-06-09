package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.common.exception.EntityNotExistException;
import roomescape.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentKey(String paymentKey);

    Optional<Payment> findByReservationId(Long reservationId);

    default Payment fetchByReservationId(Long reservationId) {
        return findByReservationId(reservationId).orElseThrow(() -> new EntityNotExistException("해당 예약의 결제 정보가 없습니다."));
    }
}
