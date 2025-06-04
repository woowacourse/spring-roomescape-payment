package roomescape.client;

import roomescape.domain.payment.Payment;
import roomescape.dto.reservation.PaymentConfirmDto;

public interface PaymentClient {

    Payment confirmPayment(PaymentConfirmDto requestDto);
}
