package roomescape.domain.reservation;

import jakarta.persistence.Embeddable;
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
    private ReservationTime reservationTime;

    @ManyToOne(optional = false)
    private Theme theme;

    protected ReservationInfo() {
    }

    public ReservationInfo(LocalDate date, ReservationTime reservationTime, Theme theme) {
        this.date = date;
        this.reservationTime = reservationTime;
        this.theme = theme;
    }

    public ReservationInfo(LocalDate date, Long reservationTimeId, Long themeId) {
        this(date, new ReservationTime(reservationTimeId), new Theme(themeId));
    }

    public boolean isPast(LocalDateTime now) {
        LocalDateTime dateTime = date.atTime(reservationTime.getStartAt());
        return dateTime.isBefore(now);
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getReservationTime() {
        return reservationTime;
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
        return Objects.equals(date, that.date) && Objects.equals(reservationTime, that.reservationTime)
                && Objects.equals(theme, that.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, reservationTime, theme);
    }
}
