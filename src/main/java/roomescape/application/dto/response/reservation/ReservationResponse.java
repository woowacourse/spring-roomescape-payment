package roomescape.application.dto.response.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.application.dto.response.member.MemberResponse;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;

public record ReservationResponse(
        Long id,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        MemberResponse member,
        Status status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()),
                MemberResponse.from(reservation.getMember()),
                reservation.getStatus()
        );
    }
}
