package roomescape.dto.response.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal totalAmount
) {
    public static MyReservationResponse of(Reservation reservation, String paymentKey, BigDecimal totalAmount,
                                           long rank) {
        return new MyReservationResponse(
                reservation.getId(), reservation.getTheme().getName(), reservation.getDate(),
                reservation.getTime().getStartAt(), getWaitingOrder(reservation.getStatus(), rank),
                paymentKey, totalAmount
        );
    }

    private static String getWaitingOrder(Status status, long rank) {
        if (status == Status.WAITING) {
            return rank + "번째 " + status.getValue();
        }
        return status.getValue();
    }
}
