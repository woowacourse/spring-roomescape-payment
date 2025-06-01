package roomescape.booking.reservation;

import org.springframework.stereotype.Component;
import roomescape.booking.reservation.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentConfirmCommand;

@Component
public class TossPaymentConfirmCommandFactory {

    public TossPaymentConfirmCommand toPaymentConfirmCommand(ReservationPaymentRequest request) {
        return new TossPaymentConfirmCommand(request.orderId(), request.amount(), request.paymentKey());
    }
}
