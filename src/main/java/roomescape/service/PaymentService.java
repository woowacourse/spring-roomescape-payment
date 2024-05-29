package roomescape.service;

import roomescape.dto.request.reservation.ReservationRequest;

public interface PaymentService {
    ReservationRequest pay(ReservationRequest reservationRequest);
}
