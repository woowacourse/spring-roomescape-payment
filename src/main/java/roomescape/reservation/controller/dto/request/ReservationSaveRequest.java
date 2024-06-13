package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.service.dto.request.ReservationPaymentSaveRequest;

public record ReservationSaveRequest(
        @NotNull long memberId,
        @NotNull LocalDate date,
        @NotNull long themeId,
        @NotNull long timeId
) {

    public static ReservationSaveRequest from(ReservationPaymentSaveRequest request) {
        return new ReservationSaveRequest(
                request.memberId(),
                request.date(),
                request.themeId(),
                request.timeId()
        );
    }

    public Reservation toReservation(Member member, Theme theme, ReservationTime reservationTime) {
        return new Reservation(member, date, theme, reservationTime);
    }
}
