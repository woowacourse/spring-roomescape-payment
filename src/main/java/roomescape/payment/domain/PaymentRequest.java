package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import roomescape.reservation.domain.PaymentStatus;

@Entity
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public PaymentRequest(Long id, String orderId, String paymentKey, BigDecimal amount) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = PaymentStatus.READY;
    }

    public PaymentRequest(String orderId, String paymentKey, BigDecimal amount) {
        this(null, orderId, paymentKey, amount);
    }

    protected PaymentRequest() {
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}
