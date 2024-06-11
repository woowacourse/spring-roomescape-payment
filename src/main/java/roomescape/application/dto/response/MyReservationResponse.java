package roomescape.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.dto.WaitingWithRankDto;

public record MyReservationResponse(
        Long id,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String theme,
        ReservationStatus status,
        Long rank,
        String paymentKey,
        Long amount
) {
    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getDetail().getDate(),
                reservation.getDetail().getTime().getStartAt(),
                reservation.getDetail().getTheme().getName(),
                ReservationStatus.RESERVED,
                0L,
                reservation.getPaymentKey(),
                reservation.getPaymentAmount()
        );
    }

    public static MyReservationResponse from(WaitingWithRankDto waitingWithRankDto) {
        Waiting waiting = waitingWithRankDto.waiting();
        Long rank = waitingWithRankDto.rank();

        return new MyReservationResponse(
                waiting.getId(),
                waiting.getDetail().getDate(),
                waiting.getDetail().getTime().getStartAt(),
                waiting.getDetail().getTheme().getName(),
                ReservationStatus.WAITING,
                rank,
                null,
                null
        );
    }
}
