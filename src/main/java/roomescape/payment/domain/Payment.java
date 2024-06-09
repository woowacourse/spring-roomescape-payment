package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, BigDecimal totalAmount, PaymentStatus status,
                   Long reservationId) {
        this(null, paymentKey, orderId, totalAmount, status, reservationId);
    }

    public Payment(String paymentKey, String orderId, BigDecimal totalAmount, PaymentStatus status) {
        this(null, paymentKey, orderId, totalAmount, status, null);
    }

    private Payment(Long id, String paymentKey, String orderId, BigDecimal totalAmount, PaymentStatus status,
                   Long reservationId) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.reservationId = reservationId;
    }

    public void bindToReservation(Long reservationId) {
        this.reservationId = reservationId;
    }

    public void cancel(){
        this.status = PaymentStatus.CANCELED;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public Long getReservationId() {
        return reservationId;
    }
}
