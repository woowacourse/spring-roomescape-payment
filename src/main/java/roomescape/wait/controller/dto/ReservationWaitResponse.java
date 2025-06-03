package roomescape.wait.controller.dto;

import java.time.LocalDate;
import java.util.List;
import roomescape.theme.controller.dto.ThemeResponse;
import roomescape.time.controller.dto.ReservationTimeResponse;
import roomescape.wait.domain.ReservationWait;

public record ReservationWaitResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        Long rank
) {
    public static ReservationWaitResponse from(final ReservationWait reservationWait) {
        return new ReservationWaitResponse(
                reservationWait.getId(),
                reservationWait.getMember().getName().name(),
                reservationWait.getSchedule().getDate(),
                ReservationTimeResponse.from(reservationWait.getSchedule().getReservationTime()),
                ThemeResponse.from(reservationWait.getSchedule().getTheme()),
                reservationWait.calculateRank()
        );
    }

    public static List<ReservationWaitResponse> from(final List<ReservationWait> reservationWaits) {
        return reservationWaits.stream()
                .map(ReservationWaitResponse::from)
                .toList();
    }
}
