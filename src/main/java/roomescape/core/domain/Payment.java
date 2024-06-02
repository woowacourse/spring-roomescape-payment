package roomescape.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;
    @Column(unique = true, nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private Long amount;
    @Column(unique = true, nullable = false)
    private String orderId;

    protected Payment() {
    }

    public Payment(Long id, Reservation reservation, String paymentKey, Long amount, String orderId) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
    }

    public Payment(Reservation reservation, String paymentKey, Long amount, String orderId) {
        this(null, reservation, paymentKey, amount, orderId);
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

    public Long getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }
}
