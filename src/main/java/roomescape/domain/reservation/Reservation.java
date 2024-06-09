package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import roomescape.domain.member.Member;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"date", "time_id", "theme_id"}))
public class Reservation {

    private static final int DAYS_IN_ADVANCE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Theme theme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(LocalDate date, Member member, ReservationTime time, Theme theme) {
        this(null, date, member, time, theme);
    }

    private Reservation(Long id, LocalDate date, Member member, ReservationTime time, Theme theme) {
        validate(date, member, time, theme);

        this.id = id;
        this.date = date;
        this.member = member;
        this.time = time;
        this.theme = theme;
        this.status = ReservationStatus.ACCEPTED;
    }

    private void validate(LocalDate date, Member member, ReservationTime time, Theme theme) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수 값입니다.");
        }

        if (member == null) {
            throw new IllegalArgumentException("회원은 필수 값입니다.");
        }

        if (time == null) {
            throw new IllegalArgumentException("예약 시간은 필수 값입니다.");
        }

        if (theme == null) {
            throw new IllegalArgumentException("테마는 필수 값입니다.");
        }
    }

    public void validateFutureReservation(LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time.getStartAt());
        if (reservationDateTime.isBefore(now.plusDays(DAYS_IN_ADVANCE))) {
            throw new IllegalArgumentException(String.format("예약은 최소 %d일 전에 해야합니다.", DAYS_IN_ADVANCE));
        }
    }

    public void validateOwnerNotSameAsWaitingMember(Member waitingMember) {
        if (this.member.equals(waitingMember)) {
            throw new IllegalArgumentException("예약자와 대기자가 동일합니다.");
        }
    }

    public void changeMember(Member member) {
        if (status == ReservationStatus.ACCEPTED) {
            throw new IllegalArgumentException("확정된 예약은 변경할 수 없습니다.");
        }
        accept();
        this.member = member;
    }

    public void accept() {
        if (status.isAccepted()) {
            throw new IllegalArgumentException("이미 확정된 예약입니다.");
        }
        this.status = ReservationStatus.ACCEPTED;
    }

    public void cancel() {
        if (status.isCanceled()) {
            throw new IllegalArgumentException("이미 취소된 예약입니다.");
        }
        this.status = ReservationStatus.CANCELED;
    }

    public boolean isCanceled() {
        return status.isCanceled();
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
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Member getMember() {
        return member;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }
}
