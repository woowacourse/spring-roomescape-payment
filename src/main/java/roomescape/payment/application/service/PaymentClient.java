package roomescape.payment.application.service;

import roomescape.payment.infrastructure.dto.TossPaymentResponse;
import roomescape.reservation.presentation.dto.ReservationRequest;

public interface PaymentClient {

    TossPaymentResponse approve(final ReservationRequest reservationRequest);
}
