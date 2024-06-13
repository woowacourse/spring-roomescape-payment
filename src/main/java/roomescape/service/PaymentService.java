package roomescape.service;

import roomescape.domain.reservation.Reservation;
import roomescape.dto.request.payment.PaymentRequest;
import roomescape.dto.response.payment.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest paymentRequest, Reservation reservation);
}
