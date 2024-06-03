package roomescape.infrastructure;

import roomescape.service.dto.PaymentConfirmRequest;

public interface PaymentClient {

    void confirmPayment(PaymentConfirmRequest request);
}
