package roomescape.payment.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.global.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Embedded
    private PayAmount amount;

    private long relatedId;

    public Payment(String paymentKey, PaymentType paymentType, PayAmount amount, long relatedId) {
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
        this.amount = amount;
        this.relatedId = relatedId;
    }

    protected Payment() {
    }

    public static Payment from(String paymentKey, String paymentType, long amount,
                               long relatedId) {
        return new Payment(paymentKey, PaymentType.from(paymentType), PayAmount.from(amount), relatedId);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PayAmount getAmount() {
        return amount;
    }
    public BigDecimal getAmountAsValue(){
        return amount.getAmount();
    }

    public long getRelatedId() {
        return relatedId;
    }
}

