package roomescape.model;

import jakarta.persistence.*;
import roomescape.exception.BadRequestException;
import roomescape.exception.DuplicatedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
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
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'PAYMENT_WAITING'")
    private ReservationStatus status;

    protected Reservation() {
    }

    private Reservation(Long id, LocalDate date, ReservationTime time, Theme theme, Member member, ReservationStatus status) {
        validatePast(date, time);
        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
        this.status = status;
    }

    public static Reservation of(LocalDate date, ReservationTime time, Theme theme, Member member, ReservationStatus status) {
        return new Reservation(null, date, time, theme, member, status);
    }

    public static Reservation paymentWaitingStatusOf(LocalDate date, ReservationTime time, Theme theme, Member member) {
        return new Reservation(null, date, time, theme, member, ReservationStatus.PAYMENT_WAITING);
    }

    private void validatePast(LocalDate date, ReservationTime time) {
        if (date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now()))) {
            throw new BadRequestException("현재(%s) 이전 시간으로 예약할 수 없습니다.".formatted(LocalDateTime.now()));
        }
    }

    public void changeStatus() {
        if (status == ReservationStatus.RESERVED) {
            throw new DuplicatedException("이미 결제 완료된 예약입니다.");
        }
        this.status = ReservationStatus.RESERVED;
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

    public ReservationStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) && Objects.equals(date, that.date) && Objects.equals(time, that.time) && Objects.equals(theme, that.theme) && Objects.equals(member, that.member) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, time, theme, member, status);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", theme=" + theme +
                ", member=" + member +
                ", status=" + status +
                '}';
    }
}
