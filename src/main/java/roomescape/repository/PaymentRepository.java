package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Payment;
import roomescape.service.exception.PaymentInfoNotFoundException;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(long id);

    default Payment fetchByReservationId(long id) {
        return findByReservationId(id).orElseThrow(() -> new PaymentInfoNotFoundException("존재하지 않는 결제 정보입니다."));
    }
}
