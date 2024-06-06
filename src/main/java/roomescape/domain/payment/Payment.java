package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "requested_at", nullable = false)
    private String requestedAt;

    @Column(name = "approved_at", nullable = false)
    private String approvedAt;

    public Payment(Long amount, String paymentKey, String orderId, String requestedAt, String approvedAt) {
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }
}
