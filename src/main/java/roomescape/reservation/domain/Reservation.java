package roomescape.reservation.domain;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "reservation_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.REQUESTED;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "order_id")
    private String orderId;

    public Reservation(final Member member, final ReservationSlot reservationSlot, final String orderId) {
        this.member = member;
        this.reservationSlot = reservationSlot;
        allocateOrderId(orderId);
    }

    public static Reservation createFirstReservation(final Member member, final ReservationSlot reservationSlot,
                                                     final String orderId) {
        Reservation reservation = new Reservation(member, reservationSlot, orderId);
        reservation.setReservationStatus(ReservationStatus.PENDING_PAYMENT);
        return reservation;
    }

    protected Reservation() {
    }

    public boolean isReserved() {
        return reservationSlot.findHighestPriorityMember().equals(member);
    }

    public void waitForPayment() {
        if (this.reservationStatus != ReservationStatus.REQUESTED) {
            throw new ReservationStatusException("결제 대기 상태로 변경 불가능합니다.");
        }
        this.reservationStatus = ReservationStatus.PENDING_PAYMENT;
    }

    public void confirm(final Payment payment) {
        if (this.reservationStatus != ReservationStatus.PENDING_PAYMENT) {
            throw new ReservationStatusException("확정할 수 없는 상태입니다.");
        }
        this.reservationStatus = ReservationStatus.CONFIRMED;
        this.payment = payment;
    }

    public void paymentFailed() {
        if (this.reservationStatus != ReservationStatus.PENDING_PAYMENT) {
            throw new ReservationStatusException("취소할 수 없는 상태입니다.");
        }
        this.reservationStatus = ReservationStatus.FAILED;
    }

    private void allocateOrderId(final String orderId) {
        if (orderId == null) {
            this.orderId = OrderIdGenerator.generateOrderId();
            return;
        }
        this.orderId = orderId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setReservationStatus(final ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}

