package roomescape.reservation.dto.request;

import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;

public record WaitingReservationRequest(
        Long memberId,
        LocalDate date,
        Long themeId,
        Long timeId
) {

    public static WaitingReservationRequest of(WaitingReservationSaveRequest saveRequest, Long memberId) {
        return new WaitingReservationRequest(
                memberId,
                saveRequest.date(),
                saveRequest.themeId(),
                saveRequest.timeId()
        );
    }

    public Reservation toWaitingReservation(Member member, Theme theme, ReservationTime reservationTime) {
        return new Reservation(member, date, theme, reservationTime, Status.WAIT);
    }
}
