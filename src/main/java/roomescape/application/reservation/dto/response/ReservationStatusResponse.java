package roomescape.application.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;

public record ReservationStatusResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime time,
        long waitingCount,
        String paymentKey,
        long amount
) {
    public static ReservationStatusResponse of(Reservation reservation, long waitingCount, ReservationPayment payment) {
        Theme theme = reservation.getTheme();
        ReservationTime time = reservation.getTime();
        return new ReservationStatusResponse(
                reservation.getId(),
                theme.getName(),
                reservation.getDate(),
                time.getStartAt(),
                waitingCount,
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}
