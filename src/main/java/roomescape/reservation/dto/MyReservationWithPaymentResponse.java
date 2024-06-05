package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.dto.PaymentResponse;

public record MyReservationWithPaymentResponse(Long id,
                                               String themeName,
                                               @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                               @JsonFormat(pattern = "HH:mm") LocalTime startAt,
                                               String status,
                                               Long waitingId,
                                               String paymentKey,
                                               BigDecimal amount) {
    public static MyReservationWithPaymentResponse from(MyReservationResponse reservationResponse, PaymentResponse paymentResponse) {
        return new MyReservationWithPaymentResponse(
                reservationResponse.id(),
                reservationResponse.themeName(),
                reservationResponse.date(),
                reservationResponse.startAt(),
                reservationResponse.status(),
                reservationResponse.waitingId(),
                paymentResponse.paymentKey(),
                paymentResponse.amount());
    }

    public static MyReservationWithPaymentResponse from(MyReservationResponse reservationResponse) {
        return new MyReservationWithPaymentResponse(
                reservationResponse.id(),
                reservationResponse.themeName(),
                reservationResponse.date(),
                reservationResponse.startAt(),
                reservationResponse.status(),
                reservationResponse.waitingId(),
                null,
                null);
    }
}
