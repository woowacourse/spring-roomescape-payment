package roomescape.domain.payment;

public interface ReservationPaymentRepository {

    ReservationPayment save(ReservationPayment reservationPayment);

    ReservationPayment getById(String id);
}
