package roomescape.reservation.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import roomescape.exception.ErrorType;
import roomescape.exception.RoomescapeException;

@Embeddable
public class ReservationInfo {
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIME_ID")
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "THEME_ID")
    private Theme theme;

    protected ReservationInfo() {
    }

    public ReservationInfo(LocalDate date, ReservationTime time, Theme theme) {
        validate(date, time);
        this.date = date;
        this.time = time;
        this.theme = theme;
    }


    public boolean isPast() {
        return LocalDateTime.of(this.date, this.time.getStartAt())
                .isBefore(LocalDateTime.now());
    }


    private void validate(LocalDate date, ReservationTime time) {
        if (date == null || time == null) {
            throw new RoomescapeException(ErrorType.MISSING_REQUIRED_VALUE_ERROR);
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public LocalTime getTimeValue() {
        return time.getStartAt();
    }

    public String getThemeName() {
        return theme.getName();
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final ReservationInfo that)) return false;
        return Objects.equals(date, that.date) && Objects.equals(time, that.time) && Objects.equals(theme, that.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, theme);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                ", date=" + date +
                ", time=" + time +
                ", theme=" + theme +
                '}';
    }
}
