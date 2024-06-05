package roomescape.domain.reservation;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

@Embeddable
public class ReservationInfo {
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_time_id")
    private ReservationTime time;

    @ManyToOne(optional = false)
    private Theme theme;

    protected ReservationInfo() {
    }

    public ReservationInfo(LocalDate date, ReservationTime time, Theme theme) {
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public ReservationInfo(LocalDate date, Long timeId, Long themeId) {
        this.date = date;
        this.time = new ReservationTime(timeId);
        this.theme = new Theme(themeId);
    }

    public boolean isPast(LocalDateTime now) {
        LocalDateTime dateTime = date.atTime(time.getStartAt());
        return dateTime.isBefore(now);
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReservationInfo that = (ReservationInfo) o;
        return Objects.equals(date, that.date) && Objects.equals(time, that.time)
                && Objects.equals(theme, that.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, theme);
    }
}
