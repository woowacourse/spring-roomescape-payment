package roomescape.reservation.application;

import roomescape.reservation.application.dto.PaymentRequest;
import roomescape.reservation.application.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse pay(PaymentRequest request);
}
