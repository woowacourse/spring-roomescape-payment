package roomescape.domain.payment;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PaymentKey paymentKey;

    @Embedded
    private OrderId orderId;

    @Embedded
    private Amount amount;

    private Long reservationId;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, BigDecimal amount, Long reservationId) {
        this.paymentKey = new PaymentKey(paymentKey);
        this.orderId = new OrderId(orderId);
        this.amount = new Amount(amount);
        this.reservationId = reservationId;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey.getPaymentKey();
    }

    public String getOrderId() {
        return orderId.getOrderId();
    }

    public BigDecimal getAmount() {
        return amount.getAmount();
    }

    public Long getReservationId() {
        return reservationId;
    }
}
