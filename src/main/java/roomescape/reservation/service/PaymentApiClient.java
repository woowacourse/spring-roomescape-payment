package roomescape.reservation.service;

import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.external.toss.dto.PaymentConfirmRequest;

public interface PaymentApiClient {

    PaymentInfo paymentReservation(PaymentConfirmRequest request);
}
