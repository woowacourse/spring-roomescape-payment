package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.payment.dto.response.PaymentConfirmResponse;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    protected Payment() {
    }

    public Payment(Long id, Long reservationId, String paymentKey, String orderId,
            String orderName, BigDecimal totalAmount,
            LocalDateTime requestedAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.requestedAt = requestedAt;
    }

    public Payment(PaymentConfirmResponse paymentConfirmResponse, Long reservationId) {
        this(null, reservationId, paymentConfirmResponse.paymentKey(), paymentConfirmResponse.orderId(),
                paymentConfirmResponse.orderName(), BigDecimal.valueOf(paymentConfirmResponse.totalAmount()),
                paymentConfirmResponse.requestedAt());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Payment payment = (Payment) o;
        return totalAmount.equals(payment.totalAmount) && Objects.equals(id, payment.id) && Objects.equals(
                reservationId, payment.reservationId) && Objects.equals(paymentKey, payment.paymentKey)
                && Objects.equals(orderId, payment.orderId) && Objects.equals(orderName,
                payment.orderName) && Objects.equals(requestedAt, payment.requestedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reservationId, paymentKey, orderId, orderName, totalAmount, requestedAt);
    }
}
