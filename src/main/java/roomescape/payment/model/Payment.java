package roomescape.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.reservation.model.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    public Payment(final String paymentKey, final String orderId, final Long amount, final Reservation reservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    protected Payment() {
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }
}
