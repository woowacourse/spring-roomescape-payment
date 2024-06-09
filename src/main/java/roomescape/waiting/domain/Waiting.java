package roomescape.waiting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Schedule;

@Entity
public class Waiting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Waiting(Reservation reservation, Member member) {
        this.id = null;
        this.schedule = Objects.requireNonNull(reservation).getSchedule();
        this.member = Objects.requireNonNull(member);
        this.createdAt = LocalDateTime.now();
    }

    public Waiting(Long id, Reservation reservation, Member member, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.schedule = Objects.requireNonNull(reservation).getSchedule();
        this.member = Objects.requireNonNull(member);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    protected Waiting() {
    }

    public boolean isBefore(LocalDateTime dateTime) {
        return schedule.isBefore(dateTime);
    }

    public boolean isNotWaitingOwner(Long memberId) {
        return !member.getId().equals(memberId);
    }

    public Long getId() {
        return id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Member getMember() {
        return member;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
