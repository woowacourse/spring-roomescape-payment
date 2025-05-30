package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PaymentHistory extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String paymentType;

    protected PaymentHistory() {

    }

    private PaymentHistory(Long id, String orderId, String paymentKey, String paymentType) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
    }

    public static PaymentHistory createWithoutId(
            String orderId,
            String paymentKey,
            String paymentType
    ) {
        return new PaymentHistory(null, orderId, paymentKey, paymentType);
    }
}
