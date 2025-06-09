package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.reservation.exception.ReservationStatusException;
import roomescape.reservationslot.domain.ReservationSlot;

@Entity
@Table(name = "reservations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reservation_slot_id", "member_id", "payment_id"})
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_slot_id", nullable = false)
    private ReservationSlot reservationSlot;

    @Embedded
    private CreatedAt createdAt = CreatedAt.now();

    @Embedded
    private ReservationStatus reservationStatus = ReservationStatus.requested();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public Reservation(final Member member, final ReservationSlot reservationSlot) {
        this.member = member;
        this.reservationSlot = reservationSlot;
    }

    protected Reservation() {
    }

    public boolean isReserved() {
        return reservationSlot.findHighestPriorityReservation().equals(this);
    }

    public boolean isFailed() {
        return this.reservationStatus.value() == ReservationStatus.Status.FAILED;
    }

    public void validateTransitionToPaymentPending() {
        if (reservationStatus.isFinished()) {
            throw new ReservationStatusException("결제 대기 상태로 변경 불가능합니다.");
        }
    }

    public void confirm(final Payment payment) {
        if (reservationStatus.isConfirmed()) {
            throw new ReservationStatusException("확정할 수 없는 상태입니다.");
        }
        this.reservationStatus = ReservationStatus.confirmed();
        this.payment = payment;
    }

    public void paymentFailed(final Payment payment) {
        if (reservationStatus.isFinished()) {
            throw new ReservationStatusException("취소할 수 없는 상태입니다.");
        }
        this.reservationStatus = ReservationStatus.failed();
        this.payment = payment;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof final Reservation reservation)) {
            return false;
        }
        return Objects.equals(getId(), reservation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Long getId() {
        return id;
    }

    public ReservationSlot getReservationSlot() {
        return reservationSlot;
    }

    public Member getMember() {
        return member;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public Payment getPayment() {
        return payment;
    }
}

