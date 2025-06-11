package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationWaitingRank;
import roomescape.dto.payment.PaymentResponseDto;

import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationResponseDto(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String statusMessage,
        String paymentKey,
        String orderId,
        Long amount
) {

    public MyReservationResponseDto(Reservation reservation, ReservationWaitingRank rank) {
        this(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                rank.getStatusMessage(),
                "",
                "",
                0L
        );
    }

    public MyReservationResponseDto(PaymentResponseDto payment, Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getMessage(),
                payment.paymentKey(),
                payment.orderId(),
                payment.amount()
        );
    }
}
