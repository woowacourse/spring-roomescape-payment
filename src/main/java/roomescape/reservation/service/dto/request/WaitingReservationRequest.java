package roomescape.reservation.service.dto.request;

import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.controller.dto.request.WaitingReservationSaveRequest;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;

public record WaitingReservationRequest(
        long memberId,
        LocalDate date,
        long themeId,
        long timeId
) {

    public static WaitingReservationRequest of(WaitingReservationSaveRequest saveRequest, long memberId) {
        return new WaitingReservationRequest(
                memberId,
                saveRequest.date(),
                saveRequest.themeId(),
                saveRequest.timeId()
        );
    }

    public Waiting toWaitingReservation(Member member, Theme theme, ReservationTime reservationTime) {
        return new Waiting(member, date, theme, reservationTime);
    }
}
