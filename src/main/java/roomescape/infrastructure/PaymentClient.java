package roomescape.infrastructure;

import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;

public interface PaymentClient {
    PaymentConfirmResponse getPaymentConfirmResponse(final ReservationPaymentRequest memberRequest);
}
