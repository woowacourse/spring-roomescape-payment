package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import roomescape.payment.dto.PaymentResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
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
        BigDecimal totalAmount = BigDecimal.valueOf(paymentResponse.totalAmount());
        return new Payment(paymentResponse.paymentKey(), paymentResponse.orderId(), totalAmount);
    }
}
