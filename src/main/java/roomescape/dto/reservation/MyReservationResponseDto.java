package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.waiting.ReservationWaitingRank;

public record MyReservationResponseDto(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String statusMessage,
        String paymentKey,
        Long amount
) {

    public static MyReservationResponseDto fromReservedReservation(Reservation reservation, Payment payment) {
        return new MyReservationResponseDto(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getMessage(),
                payment.getPaymentKey(),
                payment.getTotalAmount()
        );
    }

    public static MyReservationResponseDto fromWaitingReservation(Reservation reservation, ReservationWaitingRank rank) {
        return new MyReservationResponseDto(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                rank.getStatusMessage(),
                null,
                null
        );
    }
}
