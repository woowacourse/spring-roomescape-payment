package roomescape.payment.gateway;

import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.dto.request.PaymentRequest;

public interface PaymentGateway {

    PaymentMethod supports();

    void confirm(final PaymentRequest request);
}
