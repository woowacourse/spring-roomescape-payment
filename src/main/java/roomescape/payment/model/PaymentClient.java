package roomescape.payment.model;

import roomescape.reservation.model.vo.PaymentInfo;

public interface PaymentClient {

    void requestConfirm(final PaymentInfo paymentInfo);

}
