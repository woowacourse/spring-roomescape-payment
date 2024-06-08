package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationStatus;

public record MyReservationResponse(
        Long id,
        String themeName,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        ReservationStatus status,
        Long rank,
        String paymentKey,
        Long amount
) {

    public MyReservationResponse(Long id, String themeName, LocalDate date, LocalTime time, ReservationStatus status, Integer rank,
                                 String paymentKey, Long amount) {
        this(id, themeName, date, time, status, (long) rank, paymentKey, amount);
    }
}
