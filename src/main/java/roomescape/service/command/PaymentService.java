package roomescape.service.command;

import roomescape.domain.payment.Payment;
import roomescape.dto.reservation.PaymentConfirmDto;

public interface PaymentService {

    Payment confirmPayment(PaymentConfirmDto requestDto);
}
