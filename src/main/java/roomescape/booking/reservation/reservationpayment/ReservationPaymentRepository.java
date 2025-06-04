package roomescape.booking.reservation.reservationpayment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {

    ReservationPayment findByReservationId(Long reservationId);
}
