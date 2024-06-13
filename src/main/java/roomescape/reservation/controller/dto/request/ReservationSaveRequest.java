package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.service.dto.request.ReservationPaymentRequest;

public record ReservationSaveRequest(
        @NotNull Long memberId,
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        String paymentKey,
        Long amount
) {
    public static ReservationSaveRequest of(ReservationPaymentSaveRequest detail, long memberId) {
        return new ReservationSaveRequest(
                memberId,
                detail.date(),
                detail.themeId(),
                detail.timeId(),
                detail.paymentKey(),
                detail.amount()
        );
    }

    public static ReservationSaveRequest from(ReservationPaymentRequest request) {
        return new ReservationSaveRequest(
                request.memberId(),
                request.date(),
                request.themeId(),
                request.timeId(),
                request.paymentKey(),
                request.amount()
        );
    }

    public Reservation toReservation(Member member, Theme theme, ReservationTime reservationTime) {
        return new Reservation(member, date, theme, reservationTime, Status.SUCCESS);
    }
}
