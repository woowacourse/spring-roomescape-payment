package roomescape.domain.payment;

public interface ReservationPaymentRepository {

    ReservationPayment save(ReservationPayment reservationPayment);

    ReservationPayment getByOrderId(String orderId);
}
