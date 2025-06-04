package roomescape.payment.service;

import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.reservation.domain.Reservation;

public interface PaymentService {

    void registerReservation(ReservationPaymentRequest request, LoginMember loginMember);

    void confirmPayment(ReservationPaymentRequest request);

    void saveNotPaidPayment(Reservation reservation);

    void cancelPayment(Long reservationId);
}
