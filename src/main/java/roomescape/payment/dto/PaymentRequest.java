package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;
import roomescape.reservation.dto.ReservationSaveRequest;

public record PaymentRequest(
        @NotNull
        String orderId,

        @Range(min = 0, max = Integer.MAX_VALUE)
        @NotNull
        @Positive
        int amount,

        @NotNull
        String paymentKey
) {

    public static PaymentRequest from(ReservationSaveRequest reservationSaveRequest) {
        return new PaymentRequest(
                reservationSaveRequest.getOrderId(),
                reservationSaveRequest.getAmount(),
                reservationSaveRequest.getPaymentKey()
        );
    }
}
