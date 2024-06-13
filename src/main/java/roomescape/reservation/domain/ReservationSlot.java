package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import roomescape.exception.custom.BadRequestException;

@Entity
public class ReservationSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_time_id")
    private ReservationTime time;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Theme theme;

    public ReservationSlot() {
    }

    public ReservationSlot(Long id, LocalDate date, ReservationTime time, Theme theme) {
        validate(date, time);
        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public ReservationSlot(LocalDate date, ReservationTime time, Theme theme) {
        this(null, date, time, theme);
    }

    private void validate(LocalDate date, ReservationTime time) {
        if (date == null || time == null) {
            throw new BadRequestException("필수 요청값이 누락되었습니다.");
        }
    }

    public boolean isPast() {
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        return LocalDateTime.of(this.date, this.time.getStartAt()).isBefore(now);
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReservationSlot that = (ReservationSlot) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "ReservationSlot{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", theme=" + theme +
                '}';
    }
}
