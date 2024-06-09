package roomescape.reservation.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Entity
public class Reservation {
    private static final ReservationStatus DEFAULT_STATUS = ReservationStatus.WAITING_FOR_PAYMENT;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public Reservation(Member member, Schedule schedule) {
        this(null, member, schedule, DEFAULT_STATUS);
    }

    public Reservation(Member member, Schedule schedule, ReservationStatus status) {
        this(null, member, schedule, status);
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme) {
        this(null, member, new Schedule(date, time, theme), DEFAULT_STATUS);
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme, ReservationStatus status) {
        this(null, member, new Schedule(date, time, theme), status);
    }

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, Theme theme) {
        this(Objects.requireNonNull(id), member, new Schedule(date, time, theme), DEFAULT_STATUS);
    }

    private Reservation(Long id, Member member, Schedule schedule, ReservationStatus status) {
        this.id = id;
        this.member = Objects.requireNonNull(member);
        this.schedule = Objects.requireNonNull(schedule);
        this.status = Objects.requireNonNull(status);
    }

    protected Reservation() {
    }

    public boolean isBefore(LocalDateTime currentDateTime) {
        return schedule.isBefore(currentDateTime);
    }

    public void completePaying() {
        if (status.isPaid()) {
            throw new IllegalArgumentException("이미 결제된 예약입니다.");
        }
        status = ReservationStatus.DONE_PAYMENT;
    }

    public boolean isPaid() {
        return status.isPaid();
    }

    public boolean canRefund() {
        return status.isNeedRefund();
    }

    public boolean isDifferentMember(Long memberId) {
        return member.isDifferentId(memberId);
    }

    public void reconfirmReservation(Member newMember) {
        this.member = newMember;
        this.status = DEFAULT_STATUS;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public LocalDate getDate() {
        return schedule.getDate();
    }

    public ReservationTime getTime() {
        return schedule.getTime();
    }

    public Theme getTheme() {
        return schedule.getTheme();
    }

    public ReservationStatus getStatus() {
        return status;
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
        return Objects.equals(id, that.id)
                && Objects.equals(member, that.member)
                && Objects.equals(schedule, that.schedule)
                && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member, schedule, status);
    }
}
