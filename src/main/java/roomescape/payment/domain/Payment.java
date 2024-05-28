package roomescape.payment.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import roomescape.payment.dto.PaymentResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
    @CreatedDate
    private LocalDateTime createdAt;

    protected Payment() {
    }

    public Payment(Long id, String paymentKey, String orderId, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Payment(String paymentKey, String orderId, BigDecimal amount) {
        this(null, paymentKey, orderId, amount, null);
    }

    public static Payment from(PaymentResponse paymentResponse) {
        return new Payment(paymentResponse.paymentKey(), paymentResponse.orderId(), paymentResponse.totalAmount());
    }
}
