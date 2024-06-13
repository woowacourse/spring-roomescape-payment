package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Waiting;

public record ReservationResponse(
        long id,
        MemberResponse member,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        ThemeResponse theme,
        TimeResponse time,
        Status status
) {

    public static ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                MemberResponse.toResponse(reservation.getMember()),
                reservation.getDate(),
                ThemeResponse.toResponse(reservation.getTheme()),
                TimeResponse.toResponse(reservation.getTime()),
                Status.SUCCESS
        );
    }

    public static ReservationResponse toResponse(Waiting waiting) {
        return new ReservationResponse(
                waiting.getId(),
                MemberResponse.toResponse(waiting.getMember()),
                waiting.getDate(),
                ThemeResponse.toResponse(waiting.getTheme()),
                TimeResponse.toResponse(waiting.getTime()),
                waiting.getStatus()
        );
    }
}
