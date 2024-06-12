package roomescape.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Reservation;

@Schema(description = "Reservation Response Model")
public record ReservationResponse(@Schema(description = "Reservation ID", example = "123")
                                  Long id,

                                  @Schema(description = "Member information")
                                  MemberResponse member,

                                  @Schema(description = "Reservation date", example = "2024-06-30")
                                  LocalDate date,

                                  @Schema(description = "Time slot information")
                                  TimeSlotResponse time,

                                  @Schema(description = "Theme information")
                                  ThemeResponse theme) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                TimeSlotResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme())
        );
    }
}
