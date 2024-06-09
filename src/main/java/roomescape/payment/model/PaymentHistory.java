package roomescape.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import roomescape.member.model.Member;
import roomescape.reservation.model.Reservation;

@Entity
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentHistoryStatus paymentStatus;

    @OneToOne
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    protected PaymentHistory() {
    }

    public PaymentHistory(final String orderId, final String paymentKey, final Long totalAmount,
                          final LocalDateTime approvedAt, final Reservation reservation, final Member member) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
        this.paymentStatus = PaymentHistoryStatus.DONE;
        this.reservation = reservation;
        this.member = member;
    }

    public boolean hasSameReservation(final Reservation other) {
        return reservation.equals(other);
    }

    public void deleteReservation() {
        this.reservation = null;
        this.paymentStatus = PaymentHistoryStatus.RESERVATION_CANCELED;
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public PaymentHistoryStatus getPaymentStatus() {
        return paymentStatus;
    }

    public Member getMember() {
        return member;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
