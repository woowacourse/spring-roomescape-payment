package roomescape.persistence.repository;

import roomescape.model.ReservationTicket;
import roomescape.model.TossPayment;

public interface TossPaymentRepository {
    TossPayment save(TossPayment tossPayment);

    TossPayment findForReservationTicket(ReservationTicket reservationTicket);
}
