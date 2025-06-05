package roomescape.reservation.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.model.UserReservationWithPaymentInfoResponse;
import roomescape.reservation.model.entity.ReservationWaiting;

public record UserReservationServiceResponse(
        Long id,
        String themeName,
        LocalDate date,
        LocalTime time,
        String status,
        int rank,
        String paymentKey,
        Long amount
) {

    public static UserReservationServiceResponse of(UserReservationWithPaymentInfoResponse reservation) {
        return new UserReservationServiceResponse(
                reservation.id(),
                reservation.theme(),
                reservation.date(),
                reservation.time(),
                reservation.status(),
                -1,
                reservation.paymentKey(),
                reservation.amount()
        );
    }

    public static UserReservationServiceResponse of(ReservationWaiting reservationWaiting, int rank) {
        return new UserReservationServiceResponse(
                reservationWaiting.getId(),
                reservationWaiting.getTheme().getName(),
                reservationWaiting.getDate(),
                reservationWaiting.getTime().getStartAt(),
                reservationWaiting.getStatus().name(),
                rank,
                null,
                null

        );
    }
}
