package roomescape.domain.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import roomescape.domain.member.model.Member;
import roomescape.domain.reservation.exception.InvalidReserveInputException;

import java.time.LocalDate;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ReservationTime time;

    @ManyToOne
    private Theme theme;

    @ManyToOne
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReservationStatus status;

    @Embedded
    private ReservationDate date;

    protected Reservation() {
    }

    public Reservation(
            final ReservationStatus status,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        this(
                null,
                status,
                new ReservationDate(date),
                time,
                theme,
                member
        );
    }

    public Reservation(
            final Long id,
            final ReservationStatus status,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        this(
                id,
                status,
                new ReservationDate(date),
                time,
                theme,
                member
        );
    }

    private Reservation(
            final Long id,
            final ReservationStatus status,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        checkRequiredData(status, time, theme, member);
        this.id = id;
        this.status = status;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
    }

    private static void checkRequiredData(
            final ReservationStatus status,
            final ReservationTime reservationTime,
            final Theme theme,
            final Member member
    ) {
        if (status == null || reservationTime == null || theme == null || member == null) {
            throw new InvalidReserveInputException("예약 상태, 시간, 테마, 회원 정보는 Null을 입력할 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "date=" + date +
                ", id=" + id +
                ", time=" + time +
                ", theme=" + theme +
                ", member=" + member +
                ", status=" + status +
                '}';
    }

    public void cancel() {
        this.status = ReservationStatus.CANCEL;
    }

    public boolean isCancel() {
        return this.status == ReservationStatus.CANCEL;
    }

    public Long getId() {
        return id;
    }

    public ReservationDate getDate() {
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
}
