package roomescape.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
}
