package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.Reservation;
import roomescape.domain.WaitingWithRank;

public record ReservationMineResponse(long id,
                                      String theme,
                                      LocalDate date,
                                      @JsonFormat(pattern = "HH:mm") LocalTime time,
                                      String status,
                                      String paymentKey,
                                      BigDecimal amount) {

    public static ReservationMineResponse from(Reservation reservation) {
        return new ReservationMineResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getMessage(),
                reservation.getPaymentKey(),
                reservation.getTheme().getPrice()
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
                null
        );
    }
}
