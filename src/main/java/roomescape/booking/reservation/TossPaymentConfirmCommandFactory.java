package roomescape.booking.reservation;

import org.springframework.stereotype.Component;
import roomescape.payment.dto.TossPaymentConfirmCommand;
import roomescape.reservationpayment.dto.ReservationPaymentRequest;

@Component
public class TossPaymentConfirmCommandFactory {

    public TossPaymentConfirmCommand toPaymentConfirmCommand(ReservationPaymentRequest request) {
        return new TossPaymentConfirmCommand(request.orderId(), request.amount(), request.paymentKey());
    }
}
