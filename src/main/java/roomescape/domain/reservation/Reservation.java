package roomescape.domain.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import roomescape.domain.member.Member;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.ReservationTime;

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
    private Status status;

    protected Reservation() {
    }

    public Reservation(LocalDate date, ReservationTime reservationTime, Theme theme, Member member, Status status) {
        this(null, date, reservationTime, theme, member, status);
    }

    public Reservation(Long id, LocalDate date, ReservationTime reservationTime, Theme theme, Member member,
                       Status status) {
        this.id = id;
        this.date = date;
        this.time = reservationTime;
        this.theme = theme;
        this.member = member;
        this.status = status;
    }

    public void changeStatusToReserve() {
        status = Status.RESERVED;
    }

    public void changeStatusToPaymentPending() {
        status = Status.PAYMENT_PENDING;
    }

    public boolean isReserved() {
        return status == Status.RESERVED;
    }

    public boolean isPaymentPending() {
        return status == Status.PAYMENT_PENDING;
    }

    public boolean isWaiting() {
        return status == Status.WAITING;
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

    public Member getMember() {
        return member;
    }

    public Status getStatus() {
        return status;
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
