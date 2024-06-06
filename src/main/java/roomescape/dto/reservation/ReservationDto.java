package roomescape.dto.reservation;

import java.time.LocalDate;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;

public record ReservationDto(
        Long memberId,
        LocalDate date,
        Long timeId,
        Long themeId
) {

    public static ReservationDto of(ReservationSaveRequest request, Long memberId) {
        return new ReservationDto(
                memberId,
                request.date(),
                request.timeId(),
                request.themeId()
        );
    }

    public static ReservationDto of(AdminReservationSaveRequest request) {
        return new ReservationDto(
                request.memberId(),
                request.date(),
                request.timeId(),
                request.themeId()
        );
    }

    public Reservation toModel(
            final Member member,
            final ReservationTime time,
            final Theme theme,
            final ReservationStatus status
    ) {
        return new Reservation(member, date, time, theme, status);
    }
}
