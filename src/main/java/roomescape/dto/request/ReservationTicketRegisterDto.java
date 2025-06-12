package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;

public record ReservationTicketRegisterDto(

        @Schema(description = "예약을 원하는 날짜", example = "2022-03-14")
        @NotBlank String date,

        @Schema(description = "예약을 원하는 예약 시각의 ID")
        @NotNull Long timeId,

        @Schema(description = "예약을 원하는 테마의 ID")
        @NotNull Long themeId
) {

    public ReservationTicket convertToReservation(ReservationTime reservationTime, Theme theme, Member member) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return new ReservationTicket(new Reservation(parsedDate, reservationTime, theme, member, LocalDate.now()));
        } catch (DateTimeParseException e) {
            throw new IllegalStateException("날짜 형식이 잘못되었습니다", e);
        }
    }

}
