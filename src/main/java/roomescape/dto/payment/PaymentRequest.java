package roomescape.dto.payment;

import java.math.BigDecimal;
import roomescape.dto.reservation.UserReservationPaymentRequest;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount,
        String paymentType
) {
    public static PaymentRequest from(UserReservationPaymentRequest userReservationPaymentRequest) {
        return new PaymentRequest(
                userReservationPaymentRequest.paymentKey(),
                userReservationPaymentRequest.orderId(),
                userReservationPaymentRequest.amount(),
                userReservationPaymentRequest.paymentType()
        );
    }
}
