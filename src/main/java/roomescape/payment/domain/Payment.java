package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import roomescape.payment.exception.PaymentStatusException;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Embedded
    private PaymentKey paymentKey;

    @Embedded
    private OrderId orderId;

    @Embedded
    private Amount amount;

    @Column(name = "payment_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    protected Payment() {
    }

    public Payment(final String paymentKey, final String orderId, final Long amount, final PaymentType paymentType) {
        this.paymentKey = PaymentKey.from(paymentKey);
        this.orderId = OrderId.from(orderId);
        this.amount = Amount.from(amount);
        this.paymentType = paymentType;
    }

    public void approve() {
        if (paymentStatus.isFinished()) {
            throw new PaymentStatusException("결제 승인은 PENDING 상태에서만 가능합니다.");
        }
        this.paymentStatus = PaymentStatus.APPROVED;
    }

    public void fail() {
        if (paymentStatus.isFinished()) {
            throw new PaymentStatusException("결제 실패는 PENDING 상태에서만 가능합니다");
        }
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey.value();
    }

    public String getOrderId() {
        return orderId.value();
    }

    public Long getAmount() {
        return amount.value();
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
}
