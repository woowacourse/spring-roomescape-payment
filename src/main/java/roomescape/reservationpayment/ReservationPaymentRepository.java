package roomescape.reservationpayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {

    Optional<ReservationPayment> findByReservationId(Long reservationId);
}
