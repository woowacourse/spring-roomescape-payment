package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.model.Payment;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.WaitingWithRank;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "14:30", pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {
    public static MyReservationResponse of(final Reservation reservation, final Payment payment) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName().getValue(),
                reservation.getDate().getValue(),
                reservation.getTime().getStartAt(),
                "예약",
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyReservationResponse from(final WaitingWithRank waitingWithRank) {
        return new MyReservationResponse(
                waitingWithRank.getWaiting().getId(),
                waitingWithRank.getWaiting().getReservation().getTheme().getName().getValue(),
                waitingWithRank.getWaiting().getReservation().getDate().getValue(),
                waitingWithRank.getWaiting().getReservation().getTime().getStartAt(),
                waitingWithRank.getRank() + "번째 예약대기",
                null,
                null
        );
    }
}
