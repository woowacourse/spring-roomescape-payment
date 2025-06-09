package roomescape.reservation.infrastructure.vo;

import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

import java.time.LocalDate;

public record MyReservation(long id,
                            LocalDate date,
                            ReservationTime time,
                            Theme theme,
                            int sequence,
                            String paymentKey,
                            int amount
) {

    private static final String PAYMENT_WAITING = "PAYMENT_WAITING";

    public MyReservation(
            long id,
            LocalDate date,
            ReservationTime time,
            Theme theme,
            int sequence,
            int amount) {
        this(id, date, time, theme, sequence, PAYMENT_WAITING, amount);
    }
}
