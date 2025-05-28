package roomescape.reservation.service;

import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.external.toss.PaymentConfirmRequest;

public interface PaymentService {

    PaymentInfo paymentReservation(PaymentConfirmRequest request);
}
