package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationWithRank;

public record MemberReservationResponse(
        long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        long waitingRank,
        String paymentKey,
        long amount
) {
    public static MemberReservationResponse toResponse(
            ReservationWithRank reservationWithRank,
            Optional<Payment> payment
    ) {
        return payment.map(value -> getMemberReservationResponse(
                reservationWithRank, value.getPaymentKey(), value.getAmount()
        )).orElseGet(() -> getMemberReservationResponse(reservationWithRank, null, null));
    }

    private static MemberReservationResponse getMemberReservationResponse(
            ReservationWithRank reservationWithRank,
            String paymentKey, Long amount
    ) {
        Reservation reservation = reservationWithRank.getReservation();
        return new MemberReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                reservation.getStatusDisplayName(),
                reservationWithRank.getRank(),
                paymentKey,
                amount
        );
    }
}
