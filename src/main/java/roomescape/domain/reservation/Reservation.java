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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.exception.RoomEscapeException;

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

    @OneToOne(fetch = FetchType.EAGER)
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
            throw new RoomEscapeException("이미 취소된 예약입니다.");
        }
        this.status = Status.PAYMENT_PENDING;
    }

    public void toReserved() {
        if (this.isCanceled()) {
            throw new RoomEscapeException("이미 취소된 예약입니다.");
        }
        this.status = Status.RESERVED;
    }

    public void toCancel() {
        if (this.isCanceled()) {
            throw new RoomEscapeException("이미 취소된 예약입니다.");
        }
        this.status = Status.CANCELED;
    }

    public boolean isNotOwner(Long id) {
        return !this.member.getId().equals(id);
    }

    public boolean isReserved() {
        return this.status == Status.RESERVED;
    }

    public boolean isPending() {
        return this.status == Status.PAYMENT_PENDING;
    }

    public boolean isNotPending() {
        return this.status != Status.PAYMENT_PENDING;
    }

    public boolean isWaiting() {
        return this.status == Status.WAITING;
    }

    private boolean isCanceled() {
        return this.status == Status.CANCELED;
    }

    public Payment getPayment() {
        if (this.payment == null) {
            throw new RoomEscapeException("결제 정보가 없습니다.");
        }
        return payment;
    }

    public void completePayment(Payment payment) {
        if (this.isNotPending()) {
            throw new RoomEscapeException("결제 대기 상태가 아닙니다");
        }
        this.payment = payment;
        this.status = Status.RESERVED;
    }
}
