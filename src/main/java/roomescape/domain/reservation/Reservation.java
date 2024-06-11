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

    public Reservation(final LocalDate date, final ReservationTime time, final Theme theme, final Member member,
                       final Status status) {
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
        this.status = status;
    }

    public void changeToPending() {
        this.status = Status.PENDING;
    }

    public void delete() {
        this.status = Status.DELETE;
    }

    public boolean isReserved() {
        return status == Status.RESERVED;
    }

    public boolean isWaiting() {
        return this.status == Status.WAITING;
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
}
