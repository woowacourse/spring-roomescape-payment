package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.reservation.dto.UserReservationCreateRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public static PaymentConfirmRequest from(UserReservationCreateRequest request) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
