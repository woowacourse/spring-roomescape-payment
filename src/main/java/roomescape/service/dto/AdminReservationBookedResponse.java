package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.domain.reservation.dto.BookedReservationReadOnly;

public record AdminReservationBookedResponse(
        Long id,
        Long reservationId,
        String name,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        LocalTime time,
        String theme
) {

    public static AdminReservationBookedResponse from(BookedReservationReadOnly reservation) {
        return new AdminReservationBookedResponse(
                reservation.id(),
                reservation.reservationId(),
                reservation.name(),
                reservation.date(),
                reservation.time(),
                reservation.theme()
        );
    }

    public LocalDateTime dateTime() {
        return LocalDateTime.of(date, time);
    }
}
