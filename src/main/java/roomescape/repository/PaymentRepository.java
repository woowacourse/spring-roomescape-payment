package roomescape.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import roomescape.domain.payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(long reservationId);
}
