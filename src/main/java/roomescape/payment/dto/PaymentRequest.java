package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import roomescape.reservation.dto.UserReservationSaveRequest;

public record PaymentRequest(
        @NotNull
        String orderId,

        @Range(min = 0, max = Integer.MAX_VALUE)
        int amount,

        @NotNull
        String paymentKey
) {

    public static PaymentRequest from(UserReservationSaveRequest userReservationSaveRequest) {
        return new PaymentRequest(
                userReservationSaveRequest.orderId(),
                userReservationSaveRequest.amount(),
                userReservationSaveRequest.paymentKey()
        );
    }
}
