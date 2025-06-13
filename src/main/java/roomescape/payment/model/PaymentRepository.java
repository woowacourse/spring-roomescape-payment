package roomescape.payment.model;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment getLatestByReservationId(Long reservationId);

    Payment getById(Long id);
}
