package roomescape.domain.payment;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TossPayment extends BaseEntity {

    private String paymentKey;

    private String orderId;

    private long amount;

    private PaymentStatus paymentStatus;

    private TossPayment(
            final String paymentKey,
            final String orderId,
            final long amount,
            final PaymentStatus paymentStatus) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public static TossPayment init(
            final String paymentKey,
            final String orderId,
            final long amount) {
        return new TossPayment(paymentKey, orderId, amount, PaymentStatus.PENDING);
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
