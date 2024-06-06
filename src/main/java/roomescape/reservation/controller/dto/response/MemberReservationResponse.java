package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationWithRankAndPayment;

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

    public static MemberReservationResponse toResponse(ReservationWithRankAndPayment reservationWithRankAndPayment) {
        Reservation reservation = reservationWithRankAndPayment.getReservation();
        Payment payment = reservationWithRankAndPayment.getPayment();

        if (payment != null) {
            return new MemberReservationResponse(
                    reservation.getId(),
                    reservation.getThemeName(),
                    reservation.getDate(),
                    reservation.getStartAt(),
                    reservation.getStatusDisplayName(),
                    reservationWithRankAndPayment.getRank(),
                    payment.getPaymentKey(),
                    payment.getAmount()
            );
        }
        return new MemberReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                reservation.getStatusDisplayName(),
                reservationWithRankAndPayment.getRank(),
                null,
                null
        );
    }
}
