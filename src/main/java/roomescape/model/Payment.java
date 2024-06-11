package roomescape.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.response.PaymentResponse;

import java.math.BigDecimal;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private BigDecimal amount;
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public Payment() {
    }

    public Payment(final Long id, final String paymentKey, final Long amount, final Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = BigDecimal.valueOf(amount);
        this.reservation = reservation;
    }

    public Payment(PaymentResponse paymentResponse, Reservation reservation) {
        this(null, paymentResponse.getPaymentKey(), paymentResponse.getTotalAmount(), reservation);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}