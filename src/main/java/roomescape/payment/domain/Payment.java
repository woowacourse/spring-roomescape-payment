package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String orderId;

    @Getter
    private LocalDateTime paymentDateTime;

    private Long amount;

    private PaymentStatus status;

    private String paymentKey;

    public Payment(final String orderId, final LocalDateTime paymentDateTime, final Long amount, final PaymentStatus status, final String paymentKey) {
        this.orderId = orderId;
        this.paymentDateTime = paymentDateTime;
        this.amount = amount;
        this.status = status;
        this.paymentKey = paymentKey;
    }
}
