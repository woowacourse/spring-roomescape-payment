package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;
import roomescape.exception.custom.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

@Entity
public class Reservation {

    private static final Long AMOUNT = 1000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Member member;

    @NotNull
    @ManyToOne
    private ReservationSlot reservationSlot;

    @OneToOne
    private Payment payment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public Reservation() {
    }

    public Reservation(Member member,
                       ReservationSlot reservationSlot,
                       ReservationStatus status,
                       Payment payment) {
        validate(member, payment);
        this.member = member;
        this.reservationSlot = reservationSlot;
        this.status = status;
        this.payment = payment;
    }

    private void validate(Member member, Payment payment) {
        if (member.isUser() && !payment.hasSameAmount(AMOUNT)) {
            throw new BadRequestException("결제 금액이 잘못되었습니다.");
        }
    }

    public Reservation(Long id, Member member, ReservationSlot reservationSlot) {
        this.id = id;
        this.member = member;
        this.reservationSlot = reservationSlot;
    }

    public Reservation(Member member, ReservationSlot reservationSlot, Payment payment) {
        this.member = member;
        this.reservationSlot = reservationSlot;
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.BOOKED;
        this.payment = payment;
    }

    public boolean isBookedBy(Member member) {
        return this.member.equals(member);
    }

    public void bookReservation() {
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

    public Payment getPayment() {
        return payment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Long getAmount() {
        return AMOUNT;
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
