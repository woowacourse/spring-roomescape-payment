package roomescape.payment.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import roomescape.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);
}
