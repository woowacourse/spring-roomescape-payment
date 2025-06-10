package roomescape.payment.toss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.toss.domain.TossPayment;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {

    TossPayment findByReservation_Id(Long reservationId);
}
