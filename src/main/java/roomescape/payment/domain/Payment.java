package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    PaymentKey paymentKey;

    @Embedded
    OrderId orderId;

    @Embedded
    Amount amount;

    @Column(nullable = false)
    Long reservationId;

    public Payment(PaymentKey paymentKey, OrderId orderId, Amount amount, Long reservationId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservationId = reservationId;
    }
}
