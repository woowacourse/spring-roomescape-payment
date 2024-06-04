package roomescape.payment.domain;

import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;

public interface PaymentClient {
    PaymentConfirmResponse confirm(PaymentConfirmRequest request);
}
