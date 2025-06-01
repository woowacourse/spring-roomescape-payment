package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String paymentType;

    protected Payment() {

    }

    public Payment(Long id, String orderId, String paymentKey, String paymentType) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
    }

    public static Payment createWithoutId(
            String orderId,
            String paymentKey,
            String paymentType
    ) {
        return new Payment(null, orderId, paymentKey, paymentType);
    }
}
