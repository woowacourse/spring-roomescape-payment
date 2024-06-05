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

    @Column(unique = true, nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    @Column(unique = true, nullable = false)
    private String orderId;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public Payment(Long id, String paymentKey, Long amount, String orderId, Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
        this.reservation = reservation;
    }

    public Payment(String paymentKey, Long amount, String orderId, Reservation reservation) {
        this(null, paymentKey, amount, orderId, reservation);
    }

    public Payment() {
    }

    public Long getId() {
        return id;
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

    public Reservation getReservation() {
        return reservation;
    }
}
