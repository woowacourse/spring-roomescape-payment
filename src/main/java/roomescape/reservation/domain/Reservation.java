package roomescape.reservation.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;

@Entity
public class Reservation {

    private static final Long AMOUNT = 1000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @OneToOne
    private Payment payment;

    @ManyToOne
    private ReservationSlot reservationSlot;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public Reservation() {
    }

    public Reservation(Member member,
                       ReservationSlot reservationSlot,
                       ReservationStatus status) {
        this.member = member;
        this.reservationSlot = reservationSlot;
        this.status = status;
    }

    public Reservation(Long id, Member member, ReservationSlot reservationSlot) {
        this.id = id;
        this.member = member;
        this.reservationSlot = reservationSlot;
    }

    public Reservation(Member member, ReservationSlot reservationSlot) {
        this.member = member;
        this.reservationSlot = reservationSlot;
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.BOOKED;
    }

    public Reservation(Member member,
                       ReservationSlot reservationSlot,
                       ReservationStatus reservationStatus,
                       Payment payment) {
        this.member = member;
        this.reservationSlot = reservationSlot;
        this.status = reservationStatus;
        this.payment = payment;
    }

    public boolean isBookedBy(Member member) {
        return this.member.equals(member);
    }

    public boolean isNotBookedBy(Member member) {
        return !this.isBookedBy(member);
    }

    public void pendingReservation() {
        this.status = ReservationStatus.PENDING;
    }

    public void payReservation(Payment payment) {
        this.payment = payment;
        this.status = ReservationStatus.BOOKED;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public ReservationSlot getReservationSlot() {
        return reservationSlot;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Long getAmount() {
        return AMOUNT;
    }

    public Payment getPayment() {
        return payment;
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
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", member=" + member +
                ", reservationSlot=" + reservationSlot +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
