package roomescape.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Duration {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Duration(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Duration ofLastWeek() {
        return Duration.ofLastWeek(LocalDate.now());
    }

    public static Duration ofLastWeek(LocalDate baseDate) {
        return new Duration(
                baseDate.minusDays(7),
                baseDate.minusDays(1)
        );
    }

    public boolean contains(LocalDate date) {
        return !this.startDate.isAfter(date)
                && !this.endDate.isBefore(date);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration = (Duration) o;
        return Objects.equals(startDate, duration.startDate) && Objects.equals(endDate, duration.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
}
