package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.domain.reservation.dto.BookedReservationReadOnly;

public record ReservationBookedResponse(
        Long id,
        String name,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        LocalTime time,
        String theme,
        String status
) {

    public static ReservationBookedResponse from(BookedReservationReadOnly reservation) {
        return new ReservationBookedResponse(
                reservation.id(),
                reservation.name(),
                reservation.date(),
                reservation.time(),
                reservation.theme(),
                "예약 확정"
        );
    }

    public LocalDateTime dateTime() {
        return LocalDateTime.of(date, time);
    }
}
