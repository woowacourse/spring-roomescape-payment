package roomescape.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;

public record ReservationSaveRequest(
        @NotNull Long memberId,
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount
) {

    public static ReservationSaveRequest of(ReservationDetailRequest detail, Long memberId) {
        return new ReservationSaveRequest(
                memberId,
                detail.date(),
                detail.themeId(),
                detail.timeId(),
                detail.paymentKey(),
                detail.orderId(),
                detail.amount()
        );
    }

    public Reservation toReservation(Member member, Theme theme, ReservationTime reservationTime) {
        return new Reservation(member, date, theme, reservationTime, Status.SUCCESS);
    }
}
