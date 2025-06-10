package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;
import roomescape.infrastructure.error.exception.PaymentException;

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
        if (paymentStatus.canBeApproved()) {
            paymentStatus = PaymentStatus.APPROVED;
            return;
        }
        throw new PaymentException("%s 상태에서 %s 상태로 변경될 수 없습니다"
                .formatted(this.paymentStatus, PaymentStatus.APPROVED));
    }

    public void fail() {
        if (paymentStatus.canBeFailed()) {
            paymentStatus = PaymentStatus.FAILED;
        }
        throw new PaymentException("%s 상태에서 %s 상태로 변경될 수 없습니다"
                .formatted(this.paymentStatus, PaymentStatus.FAILED));
    }

}
