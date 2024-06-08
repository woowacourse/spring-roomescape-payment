package roomescape.service.member.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.config.DateFormatConstraint;
import roomescape.config.TimeFormatConstraint;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationWaiting;
import roomescape.domain.reservation.WaitingWithRank;

public record MemberReservationResponse(
        Long reservationId,
        String theme,
        @DateFormatConstraint LocalDate date,
        @TimeFormatConstraint LocalTime time,
        String status,
        String paymentKey,
        BigDecimal paymentAmount
) {
    public static MemberReservationResponse from(Reservation reservation) {
        return new MemberReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName().getValue(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getStatus().getDescription(),
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getAmount()
        );
    }

    public static MemberReservationResponse from(WaitingWithRank waitingWithRank) {
        ReservationWaiting waiting = waitingWithRank.waiting();
        return new MemberReservationResponse(
                waiting.getId(),
                waiting.getTheme().getName().getValue(),
                waiting.getScheduleDate(),
                waiting.getSchedule().getTime(),
                waitingWithRank.rank() + "번째 예약대기",
                waiting.getPayment().getPaymentKey(),
                waiting.getPayment().getAmount()
        );
    }
}
