package roomescape.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import roomescape.exception.BadRequestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "reservation")
@SQLDelete(sql = "UPDATE reservation set deleted = true WHERE id = ?")
@SQLRestriction("deleted <> true")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @ManyToOne(fetch = FetchType.LAZY)
    private ReservationTime time;
    @ManyToOne(fetch = FetchType.LAZY)
    private Theme theme;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @ColumnDefault("false")
    private Boolean deleted = false;

    protected Reservation() {
    }

    public Reservation(final Long id, final LocalDate date, final ReservationTime time, final Theme theme, final Member member) {
        validatePast(date, time);
        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
    }

    public Reservation(final LocalDate date, final ReservationTime time, final Theme theme, final Member member) {
        this(null, date, time, theme, member);
    }

    private void validatePast(final LocalDate date, final ReservationTime time) {
        if (date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now()))) {
            throw new BadRequestException("현재(%s) 이전 시간으로 예약할 수 없습니다.".formatted(LocalDateTime.now()));
        }
    }

    public long getId() {
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

    public Member getMember() {
        return member;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Reservation that = (Reservation) object;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getDate(), that.getDate())
                && Objects.equals(getTime(), that.getTime()) && Objects.equals(getTheme(),
                that.getTheme()) && Objects.equals(getMember(), that.getMember());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDate(), getTime(), getTheme(), getMember());
    }
}
