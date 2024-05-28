package roomescape.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import roomescape.member.model.Member;

import java.time.LocalDateTime;

@Entity
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Column(nullable = false)
    private String paymentProvider;

    @ManyToOne
    private Member member;

    protected PaymentHistory() {
    }

    public PaymentHistory(
            final String orderId,
            final PaymentStatus paymentStatus,
            final String orderName,
            final Long totalAmount,
            final LocalDateTime approvedAt,
            final String paymentProvider,
            final Member member
    ) {
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.approvedAt = approvedAt;
        this.paymentProvider = paymentProvider;
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public Member getMember() {
        return member;
    }
}
