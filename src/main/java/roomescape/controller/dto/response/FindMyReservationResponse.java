package roomescape.controller.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.domain.reservation.Reservation;

public record FindMyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        boolean existsPayment,
        String paymentKey,
        long amount,
        Long rank) {
    public static FindMyReservationResponse from(Reservation reservation, Long rank) {
        boolean existsPayment = reservation.getPayment() != null;
        String paymentKey = "";
        if (existsPayment) {
            paymentKey = reservation.getPayment().getPaymentKey();
        }
        long amount = 0;
        if (existsPayment) {
            amount = reservation.getPayment().getAmount();
        }

        return new FindMyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().toString(),
                existsPayment,
                paymentKey,
                amount,
                rank
        );
    }
}
