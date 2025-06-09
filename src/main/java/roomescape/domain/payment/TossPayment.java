package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TossPayment extends BaseEntity {

    private Long paymentId;

    private String paymentKey;

    private String orderId;

    private long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private TossPayment(
            final Long paymentId,
            final String paymentKey,
            final String orderId,
            final long amount,
            final PaymentStatus paymentStatus) {
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public static TossPayment init(
            final Long paymentId,
            final String paymentKey,
            final String orderId,
            final long amount) {
        return new TossPayment(paymentId, paymentKey, orderId, amount, PaymentStatus.PENDING);
    }

    public void approve() {
        paymentStatus = PaymentStatus.APPROVED;
    }

    public void fail() {
        paymentStatus = PaymentStatus.FAILED;
    }

    public boolean isApproved() {
        return paymentStatus.isApproved();
    }

    public String getStatusDescription() {
        return paymentStatus.getDescription();
    }
}
