package roomescape.reservation.payment.gateway;

import roomescape.reservation.payment.domain.PaymentMethod;
import roomescape.reservation.payment.dto.request.PaymentRequest;

public interface PaymentGateway {

    PaymentMethod supports();

    void confirm(final PaymentRequest request);
}
