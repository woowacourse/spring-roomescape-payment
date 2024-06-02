package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.exception.reservation.CancelReservationException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationDetail detail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Reservation(Member member, ReservationDetail detail, Status status) {
        this.member = member;
        this.detail = detail;
        this.status = status;
    }

    public void toPending() {
        if (this.isCanceled()) {
            throw new CancelReservationException("이미 취소된 예약입니다.");
        }
        this.status = Status.PAYMENT_PENDING;
    }

    public void cancel(Long memberId) {
        if (this.isNotOwner(memberId)) {
            throw new CancelReservationException("다른 회원의 예약을 취소할 수 없습니다.");
        }
        this.status = Status.CANCELED;
    }

    public void cancelByAdmin() {
        if (this.isCanceled()) {
            throw new CancelReservationException("이미 취소된 예약입니다.");
        }
        this.status = Status.CANCELED;
    }

    public boolean isOwner(Long id) {
        return this.member.getId().equals(id);
    }

    public boolean isNotOwner(Long id) {
        return !this.member.getId().equals(id);
    }

    public boolean isReserved() {
        return this.status == Status.RESERVED;
    }

    public boolean isPaymentPending() {
        return this.status == Status.PAYMENT_PENDING;
    }

    public boolean isWaiting() {
        return this.status == Status.WAITING;
    }

    private boolean isCanceled() {
        return this.status == Status.CANCELED;
    }

    public Optional<Payment> getPayment() {
        return Optional.ofNullable(payment);
    }

    public void completePayment(Payment payment) {
        this.payment = payment;
        this.status = Status.RESERVED;
    }
}
