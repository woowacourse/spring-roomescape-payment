package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Entity
@Table(name = "schedule", uniqueConstraints = @UniqueConstraint(columnNames = {"date", "timeId", "themeId"}))
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate date;
    @ManyToOne(optional = false)
    @JoinColumn(name = "time_id")
    private ReservationTime time;
    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    public Schedule(LocalDate date, ReservationTime time, Theme theme) {
        this.id = null;
        this.date = Objects.requireNonNull(date);
        this.time = Objects.requireNonNull(time);
        this.theme = Objects.requireNonNull(theme);
    }

    protected Schedule() {
    }

    public boolean isBefore(LocalDateTime currentDateTime) {
        LocalDate currentDate = currentDateTime.toLocalDate();
        if (date.isBefore(currentDate)) {
            return true;
        }
        if (date.isAfter(currentDate)) {
            return false;
        }
        return time.isBefore(currentDateTime.toLocalTime());
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Schedule schedule = (Schedule) object;
        return Objects.equals(id, schedule.id)
                && Objects.equals(date, schedule.date)
                && Objects.equals(time, schedule.time)
                && Objects.equals(theme, schedule.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, time, theme);
    }
}
