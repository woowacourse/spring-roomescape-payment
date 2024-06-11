package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;
import roomescape.reservation.domain.AuditedEntity;

@Entity
@Table(name = "payment_request")
public class PaymentWAL extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentWALStatus status;

    @Column(nullable = false)
    private String paymentKey;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String message;

    public PaymentWAL() {
    }

    public PaymentWAL(PaymentWALStatus status, String paymentKey) {
        this(null, status, paymentKey, null);
    }

    private PaymentWAL(Long id, PaymentWALStatus status, String paymentKey, LocalDateTime updatedAt) {
        this.id = id;
        this.status = status;
        this.paymentKey = paymentKey;
        this.updatedAt = updatedAt;
    }

    public void updateStatus(PaymentWALStatus status) {
        this.status = status;
    }

    public void updateStatusPaySuccess() {
        this.status = PaymentWALStatus.PAY_CONFIRMED;
    }

    public void updateStatusPayRejected() {
        this.status = PaymentWALStatus.PAY_REJECTED;
    }

    public void setErrorMessage(String message) {
        this.message = message;
    }
}

