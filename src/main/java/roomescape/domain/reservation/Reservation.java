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
import java.time.LocalDate;
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
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.member.AuthenticationFailureException;
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
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_time_id")
    private ReservationTime time;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Reservation(Member member, Theme theme, LocalDate date, ReservationTime time, Status status) {
        this.member = member;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public void toPending() {
        if (this.isCanceled()) {
            throw new CancelReservationException();
        }
        this.status = Status.PAYMENT_PENDING;
    }

    public void toReserved() {
        if (this.isCanceled()) {
            throw new CancelReservationException();
        }
        this.status = Status.RESERVED;
    }

    public void cancel(Long memberId) {
        if (!member.hasSameId(memberId)) {
            throw new CancelReservationException("다른 회원의 예약을 취소할 수 없습니다.");
        }
        this.status = Status.CANCELED;
    }

    public void cancel() {
        this.status = Status.CANCELED;
    }

    public void validateOwner(Long memberId) {
        if (!member.hasSameId(memberId)) {
            throw new AuthenticationFailureException();
        }
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

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
