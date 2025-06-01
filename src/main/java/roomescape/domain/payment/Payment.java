package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import roomescape.domain.BaseEntity;
import roomescape.infrastructure.error.exception.PaymentException;

@Entity
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private String orderId;

    private long amount;

    public Payment(String paymentKey, String orderId, long amount) {
        this(null, paymentKey, orderId, amount);
    }

    public Payment(String orderId, long amount) {
        this(null, null, orderId, amount);
    }

    public Payment(Long id, String paymentKey, String orderId, long amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    protected Payment() {
    }

    public void validateApprovalAmount(long approvalAmount) {
        if (this.amount != approvalAmount) {
            throw new PaymentException(
                    "결제 금액(%,d)과 승인 요청 금액(%,d)이 일치하지 않아 결제 승인을 거부합니다.".formatted(this.amount, approvalAmount)
            );
        }
    }

    public void approvePayment(String paymentKey) {
        if (this.paymentKey != null) {
            throw new PaymentException("이미 결제가 승인되었습니다.");
        }
        this.paymentKey = paymentKey;
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

    public Long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
