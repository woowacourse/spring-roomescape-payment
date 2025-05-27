package roomescape.reservation.infrastructure;

import roomescape.reservation.infrastructure.dto.PaymentRequest;
import roomescape.reservation.infrastructure.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse pay(PaymentRequest request);
}
