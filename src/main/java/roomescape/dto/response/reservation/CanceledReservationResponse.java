package roomescape.dto.response.reservation;

import java.time.LocalDate;
import roomescape.domain.reservation.CanceledReservation;
import roomescape.domain.reservation.Status;
import roomescape.dto.response.member.MemberResponse;
import roomescape.dto.response.theme.ThemeResponse;

public record CanceledReservationResponse(
        long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        String status
) {
    public static CanceledReservationResponse from(CanceledReservation canceledReservation) {
        return new CanceledReservationResponse(
                canceledReservation.getId(),
                MemberResponse.from(canceledReservation.getMember()),
                canceledReservation.getDate(),
                ReservationTimeResponse.from(canceledReservation.getTime()),
                ThemeResponse.from(canceledReservation.getTheme()),
                getPrintStatus(canceledReservation.getStatus())
        );
    }

    private static String getPrintStatus(Status status) {
        return status.getValue();
    }
}
