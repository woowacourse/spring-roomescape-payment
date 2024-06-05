package roomescape.payment.domain;

import jakarta.persistence.Column;
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

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    protected Payment() {
    }

    public void bindToReservation(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
