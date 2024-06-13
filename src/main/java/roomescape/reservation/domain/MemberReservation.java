package roomescape.reservation.domain;

import jakarta.persistence.*;

import java.util.Objects;
import roomescape.global.entity.BaseEntity;
import roomescape.member.domain.Member;

@Entity
public class MemberReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Embedded
    private ReservationInfo reservation;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    protected MemberReservation() {
    }

    public MemberReservation(Long id, Member member, ReservationInfo reservation, ReservationStatus reservationStatus) {
        this.id = id;
        this.member = member;
        this.reservation = reservation;
        this.reservationStatus = reservationStatus;
    }

    public MemberReservation(Member member, ReservationInfo reservation, ReservationStatus reservationStatus) {
        this(null, member, reservation, reservationStatus);
    }

    public boolean isPending() {
        return this.reservationStatus.equals(ReservationStatus.PENDING);
    }

    public void approve() {
        if (this.reservationStatus != ReservationStatus.NOT_PAID) {
            throw new IllegalStateException("결제 대기 상태에서만 가능합니다.");
        }
        this.reservationStatus = ReservationStatus.APPROVED;
    }

    public void deny() {
        this.reservationStatus = ReservationStatus.DENY;
    }

    public void notPaid() {
        this.reservationStatus = ReservationStatus.NOT_PAID;
    }

    public boolean isNotEqualMember(Member member) {
        return !this.member.equals(member);
    }
    public boolean canDelete(Member member) {
        return member.isAdmin() || isRegisteredMember(member);
    }

    private boolean isRegisteredMember(Member member) {
        return this.member.equals(member);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public ReservationInfo getReservation() {
        return reservation;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemberReservation)) {
            return false;
        }
        MemberReservation that = (MemberReservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "MemberReservation{" +
                "id=" + id +
                ", member=" + member +
                ", reservation=" + reservation +
                ", reservationStatus=" + reservationStatus +
                '}';
    }
}
