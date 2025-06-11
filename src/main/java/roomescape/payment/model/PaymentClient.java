package roomescape.payment.model;

import roomescape.payment.application.dto.response.TossPaymentsResponse;
import roomescape.reservation.model.vo.PaymentInfo;

public interface PaymentClient {

    TossPaymentsResponse requestConfirm(final PaymentInfo paymentInfo);
}
