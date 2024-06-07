package roomescape.service;

import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest paymentRequest, Reservation reservation);
}
