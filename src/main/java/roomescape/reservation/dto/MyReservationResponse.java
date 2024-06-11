package roomescape.reservation.dto;

import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.dto.WaitingReservationRanking;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationResponse(
        long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {

    public static MyReservationResponse from(MemberReservation memberReservation) {
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                memberReservation.getStatus().getStatusName(),
                null,
                null
        );
    }

    public static MyReservationResponse from(Payment payment) {
        MemberReservation memberReservation = payment.getMemberReservation();
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                memberReservation.getStatus().getStatusName(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyReservationResponse from(WaitingReservationRanking waitingReservationRanking) {
        MemberReservation memberReservation = waitingReservationRanking.getMemberReservation();
        String status = waitingReservationRanking.getDisplayRank() + "번째 " + memberReservation.getStatus().getStatusName();
        Reservation reservation = memberReservation.getReservation();

        return new MyReservationResponse(
                memberReservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                status,
                null,
                null
        );
    }
}
