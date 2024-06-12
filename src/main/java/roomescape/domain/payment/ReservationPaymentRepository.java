package roomescape.domain.payment;

import java.util.Optional;

public interface ReservationPaymentRepository {

    ReservationPayment save(ReservationPayment reservationPayment);

    ReservationPayment getById(String id);

    Optional<ReservationPayment> findByReservationId(long id);
}
