package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.PaymentInfo;
import roomescape.service.exception.PaymentInfoNotFoundException;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentInfo, Long> {

    Optional<PaymentInfo> findByReservationId(long id);

    default PaymentInfo fetchByReservationId(long id) {
        return findByReservationId(id).orElseThrow(() -> new PaymentInfoNotFoundException("존재하지 않는 결제 정보입니다."));
    }
}
