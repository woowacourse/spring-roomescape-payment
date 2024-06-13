package roomescape.system.payment;

import roomescape.reservation.dto.request.ReservationRequest;

public interface PaymentClient {

    void confirm(final String authorizations, final ReservationRequest reservationRequest);
}
