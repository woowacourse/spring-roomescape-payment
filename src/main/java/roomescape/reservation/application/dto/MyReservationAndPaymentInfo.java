package roomescape.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.PaymentInfo;

public record MyReservationAndPaymentInfo(
    Long id,
    String theme,
    LocalDate date,
    @JsonFormat(pattern = "HH:mm")
    LocalTime time,
    String status,
    String paymentKey,
    Long amount
) {

    public static MyReservationAndPaymentInfo from(MyReservation reservation, PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return new MyReservationAndPaymentInfo(
                reservation.id(),
                reservation.theme(),
                reservation.date(),
                reservation.time(),
                reservation.status(),
                null,
                null
            );
        }

        return new MyReservationAndPaymentInfo(
            reservation.id(),
            reservation.theme(),
            reservation.date(),
            reservation.time(),
            reservation.status(),
            paymentInfo.getPaymentKey(),
            paymentInfo.getAmount()
        );
    }
}
