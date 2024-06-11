package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookedPaymentResponse(
        Long id,
        String name,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime time,
        String theme,
        String status
) {
        public static BookedPaymentResponse of(AdminReservationBookedResponse reservation, boolean isDone) {
                return new BookedPaymentResponse(
                        reservation.id(),
                        reservation.name(),
                        reservation.date(),
                        reservation.time(),
                        reservation.theme(),
                        isDone ? "예약" : "결제 대기"
                );
        }
}
