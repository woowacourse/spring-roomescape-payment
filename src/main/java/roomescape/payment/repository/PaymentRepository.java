package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    void deleteByReservationId(Long reservationId);
}
