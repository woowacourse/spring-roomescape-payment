package roomescape.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.PaymentInfo;
import roomescape.domain.reservation.Reservation;

public record FindMyReservationResponse(
    Long id,
    String theme,
    LocalDate date,
    @JsonFormat(pattern = "HH:mm") LocalTime time,
    String status,
    Long rank,
    @Nullable String paymentKey,
    @Nullable Long amount,
    @Nullable String payMethod
) {

    public static FindMyReservationResponse from(Reservation reservation, Long rank, PaymentInfo paymentInfo) {
        return new FindMyReservationResponse(
            reservation.getId(),
            reservation.getTheme().getName(),
            reservation.getDate(),
            reservation.getTime().getStartAt(),
            reservation.getStatus().toString(),
            rank,
            paymentInfo.getPaymentKey(),
            paymentInfo.getAmount(),
            paymentInfo.getPayMethod()
        );
    }
}
