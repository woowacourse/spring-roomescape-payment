package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.common.domain.AuditedEntity;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentCurrency currency;

    @Column(nullable = false)
    private int totalAmount;

    protected Payment() {
    }

    public Payment(Reservation reservation, String paymentKey, String orderId, PaymentStatus status,
                   PaymentMethod method,
                   PaymentCurrency currency, int totalAmount) {
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.status = status;
        this.method = method;
        this.currency = currency;
        this.totalAmount = totalAmount;
    }

    public Payment(Long id, Reservation reservation, String paymentKey, String orderId, PaymentStatus status,
                   PaymentMethod method, PaymentCurrency currency, int totalAmount) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.status = status;
        this.method = method;
        this.currency = currency;
        this.totalAmount = totalAmount;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentCurrency getCurrency() {
        return currency;
    }

    public int getTotalAmount() {
        return totalAmount;
    }
}
