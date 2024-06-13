package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.util.Optional;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Entity
@Table(name = "reservation", uniqueConstraints = @UniqueConstraint(columnNames = {"date", "time_id", "theme_id"}))
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @ManyToOne(optional = false)
    @JoinColumn(name = "time_id")
    private ReservationTime time;
    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id")
    private Theme theme;
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus;

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme,
                       ReservationStatus reservationStatus) {
        this.member = Optional.ofNullable(member).orElseThrow(() ->
                new RoomEscapeException("예약자는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        this.date = Optional.ofNullable(date).orElseThrow(() ->
                new RoomEscapeException("예약 날짜는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        this.time = Optional.ofNullable(time).orElseThrow(() ->
                new RoomEscapeException("예약 시간은 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        this.theme = Optional.ofNullable(theme).orElseThrow(() ->
                new RoomEscapeException("예약 테마는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        this.reservationStatus = Optional.ofNullable(reservationStatus).orElseThrow(() ->
                new RoomEscapeException("예약 상태는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
    }

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, Theme theme,
                       ReservationStatus reservationStatus) {
        this(member, date, time, theme, reservationStatus);
        this.id = Optional.ofNullable(id).orElseThrow(() ->
                new RoomEscapeException("예약 id는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
    }

    protected Reservation() {
    }

    public void completePayment() {
        this.reservationStatus = ReservationStatus.RESERVED;
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

    public boolean isNotPaidReservation() {
        return reservationStatus.equals(ReservationStatus.PAYMENT_PENDING);
    }

    public boolean isEqualsDate(LocalDate date) {
        return date.equals(this.date);
    }

    public Long getMemberId() {
        return member.getId();
    }

    public Long getTimeId() {
        return time.getId();
    }

    public Long getThemeId() {
        return theme.getId();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
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

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Reservation that = (Reservation) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Reservation{" +
               "id=" + id +
               ", member=" + member +
               ", date=" + date +
               ", time=" + time +
               ", theme=" + theme +
               ", reservationStatus=" + reservationStatus +
               '}';
    }
}
