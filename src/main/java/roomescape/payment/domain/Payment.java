package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    private Integer amount;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private Payment(Long id, String paymentKey, String orderId, Integer amount, PaymentStatus status,
                    Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
        this.status = status;
    }

    protected Payment() {
    }

    public static Payment createPendingPaymentWithoutId(String paymentKey, String orderId, Integer amount,
                                                        Reservation reservation) {
        return new Payment(null, paymentKey, orderId, amount, PaymentStatus.PENDING, reservation);
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

    public Integer getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void changeToFail() {
        this.status = PaymentStatus.FAIL;
    }

    public void changeToSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }
}
