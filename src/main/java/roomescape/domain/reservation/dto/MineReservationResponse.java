package roomescape.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.WaitingRankReservation;

public record MineReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        Long rank,
        String paymentKey,
        Long amount

) {

    public static MineReservationResponse from(final WaitingRankReservation waitingRankReservation) {
        return new MineReservationResponse(
                waitingRankReservation.getReservation().getId(),
                waitingRankReservation.getReservation().getTheme().getName(),
                waitingRankReservation.getReservation().getDate().date(),
                waitingRankReservation.getReservation().getReservationTime().getStartAt(),
                waitingRankReservation.getReservation().getReservationStatus().getMessage(),
                waitingRankReservation.getWaitingRank(),
                null,
                null
        );
    }

    public static MineReservationResponse from(final ReservationPayment reservationPayment) {
        return new MineReservationResponse(
                reservationPayment.getReservation().getId(),
                reservationPayment.getReservation().getTheme().getName(),
                reservationPayment.getReservation().getDate().date(),
                reservationPayment.getReservation().getReservationTime().getStartAt(),
                reservationPayment.getReservation().getReservationStatus().getMessage(),
                null,
                reservationPayment.getPaymentKey(),
                reservationPayment.getAmount()
        );
    }

}
