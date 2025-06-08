package roomescape.application.reservation.query.dto;

import roomescape.application.payment.PaymentResult;
import roomescape.domain.reservation.PaymentType;
import roomescape.domain.reservation.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationWithStatusAndPaymentResult(
        Long reservationId,
        String themeName,
        LocalDate reservationDate,
        LocalTime reservationTime,
        ReservationStatus status,
        PaymentType paymentType,
        String PaymentKey,
        Long amount
) {

    public static ReservationWithStatusAndPaymentResult from(
            final ReservationWithStatusResult reservationWithStatusResult,
            final PaymentResult paymentResult
    ) {
        return new ReservationWithStatusAndPaymentResult(
                reservationWithStatusResult.reservationId(),
                reservationWithStatusResult.themeName(),
                reservationWithStatusResult.reservationDate(),
                reservationWithStatusResult.reservationTime(),
                reservationWithStatusResult.status(),
                paymentResult.paymentType(),
                paymentResult.paymentKey(),
                paymentResult.amount()
        );
    }
}
