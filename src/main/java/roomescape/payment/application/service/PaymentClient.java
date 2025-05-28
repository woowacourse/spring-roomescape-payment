package roomescape.payment.application.service;

import roomescape.payment.domain.Payment;
import roomescape.reservation.presentation.dto.ReservationRequest;

public interface PaymentClient {

    Payment approve(final ReservationRequest reservationRequest);
}
