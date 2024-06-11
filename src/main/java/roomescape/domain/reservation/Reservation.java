package roomescape.domain.reservation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import roomescape.domain.member.Member;
import roomescape.exception.RoomEscapeBusinessException;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private BookedMember bookedMember;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitingMember> waitingMembers = new ArrayList<>();

    public Reservation(LocalDate date, ReservationTime time, Theme theme) {
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    protected Reservation() {
    }

    public BookedMember book(Member member) {
        validateAlreadyBooked();

        this.bookedMember = new BookedMember(this, member);
        return bookedMember;
    }

    private void validateAlreadyBooked() {
        if (isBooked() || hasWaiting()) {
            throw new RoomEscapeBusinessException("이미 예약한 사람이 존재합니다.");
        }
    }

    public WaitingMember addWaiting(Member member) {
        validateDuplicated(member);

        WaitingMember waitingMember = new WaitingMember(member, this);
        waitingMembers.add(waitingMember);

        return waitingMember;
    }

    private void validateDuplicated(Member member) {
        boolean isDuplicated = waitingMembers.stream()
                .anyMatch(waiting -> waiting.isMember(member));

        if (bookedMember.isMember(member) || isDuplicated) {
            throw new RoomEscapeBusinessException("중복된 예약을 할 수 없습니다.");
        }
    }

    public void cancelBooked() {
        if (hasNotWaiting()) {
            this.bookedMember = null;
            return;
        }

        WaitingMember waitingMember = waitingMembers.remove(0);
        this.bookedMember.changeMember(waitingMember.getMember());
    }

    public boolean isBooked() {
        return bookedMember != null;
    }

    private boolean hasNotWaiting() {
        return !hasWaiting();
    }

    private boolean hasWaiting() {
        return !waitingMembers.isEmpty();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
