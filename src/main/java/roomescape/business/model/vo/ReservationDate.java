package roomescape.business.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import roomescape.exception.reservation.PastDateReservationException;
import roomescape.exception.reservation.TooFarDateReservationException;

@Embeddable
public record ReservationDate(
        @Column(name = "date")
        LocalDate value
) {
    private static final int INTERVAL_FROM_NOW = 7;

    public static ReservationDate create(final LocalDate date) {
        validateInterval(date);
        validateNotPast(date);
        return new ReservationDate(date);
    }

    public static ReservationDate restore(final LocalDate date) {
        return new ReservationDate(date);
    }

    private static void validateNotPast(final LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new PastDateReservationException();
        }
    }

    private static void validateInterval(final LocalDate date) {
        long minusDays = ChronoUnit.DAYS.between(LocalDate.now(), date);
        if (minusDays > INTERVAL_FROM_NOW) {
            throw new TooFarDateReservationException(INTERVAL_FROM_NOW);
        }
    }
}
