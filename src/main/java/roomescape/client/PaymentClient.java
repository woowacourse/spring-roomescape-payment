package roomescape.client;

import roomescape.domain.payment.Payment;
import roomescape.service.dto.PaymentConfirmDto;

public interface PaymentClient {

    Payment confirmPayment(PaymentConfirmDto requestDto);
}
