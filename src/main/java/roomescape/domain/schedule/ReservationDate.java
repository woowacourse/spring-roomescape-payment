package roomescape.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class ReservationDate {
    @Column(name = "VISIT_DATE")
    private LocalDate value;

    protected ReservationDate() {
    }

    private ReservationDate(LocalDate value) {
        this.value = value;
    }

    public static ReservationDate of(LocalDate date) {
        return new ReservationDate(date);
    }

    public boolean isToday() {
        return value.equals(LocalDate.now());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReservationDate that = (ReservationDate) o;
        return Objects.equals(value, that.value);
    }

    public LocalDate getValue() {
        return value;
    }
}