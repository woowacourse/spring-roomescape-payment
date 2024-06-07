package roomescape.domain.reservation;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import roomescape.domain.member.Member;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private ReservationInfo info;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(LocalDate date, ReservationTime time, Theme theme, Member member, ReservationStatus status) {
        this(null, new ReservationInfo(date, time, theme), member, status);
    }

    public Reservation(Long id, LocalDate date, ReservationTime time, Theme theme, Member member, ReservationStatus status) {
        this(id, new ReservationInfo(date, time, theme), member, status);
    }

    public Reservation(Long id, ReservationInfo info, Member member, ReservationStatus status) {
        this.id = id;
        this.info = info;
        this.member = member;
        this.status = status;
    }

    public void changeStatusPaymentWaiting() {
        this.status = ReservationStatus.PAYMENT_WAITING;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public boolean isPaymentWaitingStatus() {
        return this.status == ReservationStatus.PAYMENT_WAITING;
    }

    public boolean isPast(LocalDateTime now) {
        return info.isPast(now);
    }

    public boolean isOwnedBy(Member otherMember) {
        return member.equals(otherMember);
    }

    public boolean isNotOwnedBy(long otherMemberId) {
        return member.isDifferentId(otherMemberId);
    }

    public Long getId() {
        return id;
    }

    public ReservationInfo getInfo() {
        return info;
    }

    public LocalDate getDate() {
        return info.getDate();
    }

    public ReservationTime getTime() {
        return info.getTime();
    }

    public Theme getTheme() {
        return info.getTheme();
    }

    public Member getMember() {
        return member;
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
}
