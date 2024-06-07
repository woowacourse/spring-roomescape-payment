package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.dto.PaymentResponse;
import roomescape.reservation.domain.PaymentStatus;

public record MyReservationWithPaymentResponse(Long id,
                                               String themeName,
                                               @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                               @JsonFormat(pattern = "HH:mm") LocalTime startAt,
                                               String status,
                                               Long waitingId,
                                               PaymentStatus paymentStatus,
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
                reservationResponse.paymentStatus(),
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
                reservationResponse.paymentStatus(),
                null,
                null);
    }
}
