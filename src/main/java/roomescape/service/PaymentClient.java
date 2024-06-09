package roomescape.service;

import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.request.PaymentConfirmRequest;

public interface PaymentClient {

    void pay(PaymentConfirmRequest paymentConfirmRequest);

    void cancel(PaymentCancelRequest paymentCancelRequest);
}
