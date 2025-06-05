package roomescape.payment.service;

import roomescape.reservation.domain.Reservation;

public interface PaymentEventProcessor {

    void saveNotPaidPayment(Reservation reservation);

    void cancelPayment(Long reservationId);

}
