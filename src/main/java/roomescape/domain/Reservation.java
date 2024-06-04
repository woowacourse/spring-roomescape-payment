package roomescape.domain;

import jakarta.persistence.*;
import roomescape.exception.RoomescapeException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import static roomescape.exception.ExceptionType.*;

@Entity
public class Reservation implements Comparable<Reservation> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate date;
    @ManyToOne
    private ReservationTime time;
    @ManyToOne
    private Theme theme;
    @ManyToOne
    private Member member;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = true)//TODO
    private String paymentKey;
    @Column(nullable = true)//TODO
    private Long amount;

    protected Reservation() {

    }

    public Reservation(long id, Reservation reservationBeforeSave) {
        this(id,
                reservationBeforeSave.date,
                reservationBeforeSave.time,
                reservationBeforeSave.theme,
                reservationBeforeSave.member,
                reservationBeforeSave.paymentKey,
                reservationBeforeSave.amount);
    }

    public Reservation(LocalDate date,
                       ReservationTime time,
                       Theme theme,
                       Member member,
                       String paymentKey,
                       long amount) {
        this(null, date, time, theme, member, paymentKey, amount);
    }

    public Reservation(Long id,
                       LocalDate date,
                       ReservationTime time,
                       Theme theme,
                       Member member,
                       String paymentKey,
                       long amount) {
        this(id, date, time, theme, member, LocalDateTime.now(), paymentKey, amount);
    }

    public Reservation(Long id,
                       LocalDate date,
                       ReservationTime time,
                       Theme theme,
                       Member member,
                       LocalDateTime createdAt,
                       String paymentKey,
                       long amount) {
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
        validateMember(member);
        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
        this.createdAt = createdAt;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    private void validateTheme(Theme theme) {
        if (theme == null) {
            throw new RoomescapeException(EMPTY_THEME);
        }
    }

    private void validateTime(ReservationTime time) {
        if (time == null) {
            throw new RoomescapeException(EMPTY_TIME);
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new RoomescapeException(EMPTY_DATE);
        }
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new RoomescapeException(EMPTY_MEMBER);
        }
    }

    public boolean isBefore(LocalDateTime base) {
        return this.getLocalDateTime().isBefore(base);
    }

    public boolean isAfter(LocalDateTime base) {
        return this.getLocalDateTime().isAfter(base);
    }

    public boolean isBetween(Duration duration) {
        return duration.contains(date);
    }

    public boolean isReservationTimeOf(long id) {
        return this.time.isIdOf(id);
    }

    public boolean isMemberIdOf(long memberId) {
        return member.getId() == memberId;
    }

    public boolean isThemeIdOf(long themeId) {
        return theme.getId() == themeId;
    }

    public boolean isSameReservation(Reservation beforeSave) {
        return this.getLocalDateTime().equals(beforeSave.getLocalDateTime())
                && this.isSameTheme(beforeSave);
    }

    public boolean isSameTheme(Reservation reservation) {
        return this.theme.equals(reservation.theme);
    }

    @Override
    public int compareTo(Reservation other) {
        LocalDateTime dateTime = LocalDateTime.of(date, time.getStartAt());
        LocalDateTime otherDateTime = LocalDateTime.of(other.date, other.time.getStartAt());
        return dateTime.compareTo(otherDateTime);
    }

    public long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.of(this.date, this.getTime());
    }

    public ReservationTime getReservationTime() {
        return time;
    }

    public LocalTime getTime() {
        return time.getStartAt();
    }

    public Theme getTheme() {
        return theme;
    }

    public Member getMember() {
        return member;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (member != null ? member.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
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
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", theme=" + theme +
                ", member=" + member +
                '}';
    }
}
