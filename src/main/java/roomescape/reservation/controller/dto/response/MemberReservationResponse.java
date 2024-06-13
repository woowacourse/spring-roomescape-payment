package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationWithPayment;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.WaitingWithRank;

public record MemberReservationResponse(
        long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        long waitingRank,
        String paymentKey,
        Long amount
) {

    public static MemberReservationResponse toResponse(ReservationWithPayment reservationWithPayment) {
        Reservation reservation = reservationWithPayment.getReservation();
        Payment payment = reservationWithPayment.getPayment();

        if (payment != null) {
            return new MemberReservationResponse(
                    reservation.getId(),
                    reservation.getThemeName(),
                    reservation.getDate(),
                    reservation.getStartAt(),
                    Status.SUCCESS.getDisplayName(),
                    0L,
                    payment.getPaymentKey(),
                    payment.getAmount()
            );
        }
        return new MemberReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                Status.SUCCESS.getDisplayName(),
                0L,
                null,
                null
        );
    }

    public static MemberReservationResponse toResponse(WaitingWithRank waitingWithRank) {
        Waiting waiting = waitingWithRank.getWaiting();

        return new MemberReservationResponse(
                waiting.getId(),
                waiting.getThemeName(),
                waiting.getDate(),
                waiting.getStartAt(),
                waiting.getStatusDisplayName(),
                waitingWithRank.getRank(),
                null,
                null
        );
    }
}
