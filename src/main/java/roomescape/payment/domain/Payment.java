package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import roomescape.global.common.TimeStamp;

@Entity
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE payment SET deleted_at = NOW() WHERE id = ?")
public class Payment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;
    private String orderId;
    private Integer amount;
    private String paymentType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    public Payment() {
        this(null, null, null, null, null);
        this.status = PaymentStatus.PENDING;
    }

    public Payment(final Long id, final String paymentKey, final String orderId, final Integer amount,
                   final String paymentType) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.status = PaymentStatus.COMPLETE;
    }

    public Payment(final String paymentKey, final String orderId, final Integer amount, final String paymentType) {
        this(null, paymentKey, orderId, amount, paymentType);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
