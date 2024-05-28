package roomescape.reservation.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReservationDate {

    @Column(nullable = false)
    private LocalDate date;

    protected ReservationDate() {
    }

    public ReservationDate(final LocalDate date) {
        this.date = date;
    }

    public LocalDate getValue() {
        return date;
    }
}
