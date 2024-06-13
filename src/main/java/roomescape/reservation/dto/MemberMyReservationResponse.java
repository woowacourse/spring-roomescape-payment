package roomescape.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationWaiting;
import roomescape.reservation.domain.ReservationWithPayment;

public record MemberMyReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {

    public MemberMyReservationResponse(ReservationWithPayment reservation) {
        this(
                reservation.getId(),
                reservation.getTheme(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getStatus().getValue(),
                reservation.getPaymentKey(),
                reservation.getTotalAmount()
        );
    }

    public MemberMyReservationResponse(ReservationWaiting reservationWaiting) {
        this(
                reservationWaiting.getId(),
                reservationWaiting.getTheme().getName(),
                reservationWaiting.getDate(),
                reservationWaiting.getTime().getStartAt(),
                reservationWaiting.getRank() + "번째 " + reservationWaiting.getStatus().getValue(),
                null,
                null
        );
    }
}
