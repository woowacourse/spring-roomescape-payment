package roomescape.booking.reservation;

import org.springframework.stereotype.Component;
import roomescape.external.tosspayment.dto.TossPaymentConfirmCommand;

@Component
public class TossPaymentConfirmCommandFactory {

    public TossPaymentConfirmCommand toPaymentConfirmCommand(roomescape.reservationpayment.dto.ReservationPaymentRequest request) {
        return new TossPaymentConfirmCommand(request.orderId(), request.amount(), request.paymentKey());
    }
}
