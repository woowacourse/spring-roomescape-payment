package roomescape.reservation.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

import roomescape.reservation.dto.PaymentResponse;

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
    private BigDecimal amount;

    @OneToOne
    private Reservation reservation;

    public static Payment of(PaymentResponse paymentResponse, Reservation reservation) {
        return new Payment(paymentResponse, reservation);
    }

    protected Payment() {
    }

    private Payment(PaymentResponse paymentResponse, Reservation reservation) {
        this.paymentKey = paymentResponse.paymentKey();
        this.orderId = paymentResponse.orderId();
        this.amount = paymentResponse.totalAmount();
        this.reservation = reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
