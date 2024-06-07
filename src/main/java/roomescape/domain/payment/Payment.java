package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.domain.reservation.Reservation;

import java.math.BigDecimal;

@Entity
public class Payment {

    private static final int MAX_PAYMENT_KEY_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_PAYMENT_KEY_LENGTH)
    private String paymentKey;

    @Embedded
    private Amount amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String paymentKey, BigDecimal amount) {
        this.paymentKey = paymentKey;
        this.amount = new Amount(amount);
    }
}
