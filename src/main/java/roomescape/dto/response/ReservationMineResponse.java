package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Reservation;
import roomescape.domain.WaitingWithRank;

@Schema(description = "Particular Member's Reservation Response Model")
public record ReservationMineResponse(@Schema(description = "Reservation ID", example = "123")
                                      long id,

                                      @Schema(description = "Theme name", example = "Escape Room Adventure")
                                      String theme,

                                      @Schema(description = "Reservation date", example = "2024-06-30")
                                      LocalDate date,

                                      @Schema(description = "Reservation time", example = "14:30")
                                      @JsonFormat(pattern = "HH:mm")
                                      LocalTime time,

                                      @Schema(description = "Reservation status", example = "BOOKING")
                                      String status,

                                      @Schema(description = "Payment key", example = "payment_key_123")
                                      String paymentKey,

                                      @Schema(description = "Amount", example = "20000")
                                      long amount) {

    public static ReservationMineResponse from(Reservation reservation) {
        return new ReservationMineResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getMessage(),
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getAmount()
        );
    }

    public static ReservationMineResponse from(WaitingWithRank waiting) {
        return new ReservationMineResponse(
                waiting.getWaiting().getId(),
                waiting.getWaiting().getTheme().getName(),
                waiting.getWaiting().getDate(),
                waiting.getWaiting().getTime().getStartAt(),
                waiting.getWaiting().getStatus().createStatusMessage(waiting.getRank()),
                null,
                0L
        );
    }
}
