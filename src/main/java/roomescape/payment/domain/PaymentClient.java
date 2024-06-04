package roomescape.payment.domain;

import roomescape.payment.dto.request.PaymentConfirmRequest;

public interface PaymentClient {
    ConfirmedPayment confirm(PaymentConfirmRequest request);
}
