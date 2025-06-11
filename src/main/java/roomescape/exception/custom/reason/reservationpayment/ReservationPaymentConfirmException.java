package roomescape.exception.custom.reason.reservationpayment;

import lombok.Getter;

@Getter
public class ReservationPaymentConfirmException extends RuntimeException {

    private final Long reservationId;
    private final String paymentKey;
    private final String orderId;

    public ReservationPaymentConfirmException(final String message, final Long reservationId, final String paymentKey, final String orderId) {
        super(message);
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
    }
}
