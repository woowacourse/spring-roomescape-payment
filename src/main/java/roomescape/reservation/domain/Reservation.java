package roomescape.reservation.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public Reservation(Member member, Schedule schedule) {
        this.id = null;
        this.member = member;
        this.schedule = schedule;
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme) {
        this.id = null;
        this.member = Objects.requireNonNull(member);
        this.schedule = new Schedule(date, time, theme);
    }

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, Theme theme) {
        this.id = Objects.requireNonNull(id);
        this.member = Objects.requireNonNull(member);
        this.schedule = new Schedule(date, time, theme);
    }

    protected Reservation() {
    }

    public boolean isBefore(LocalDateTime currentDateTime) {
        return schedule.isBefore(currentDateTime);
    }

    public void updateMember(Member other) {
        this.member = other;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
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
                && Objects.equals(schedule, that.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member, schedule);
    }
}
