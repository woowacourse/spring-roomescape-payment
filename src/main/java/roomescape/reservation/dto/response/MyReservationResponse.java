package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationResponse(
        String themeName,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        Long rank,
        String paymentKey,
        Long amount
) {

    public MyReservationResponse(String themeName, LocalDate date, LocalTime time, Integer rank, String paymentKey,
                                 Long amount) {
        this(themeName, date, time, (long) rank, paymentKey, amount);
    }
}
