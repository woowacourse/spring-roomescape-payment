package roomescape;

import jakarta.persistence.*;

import roomescape.reservation.dto.PaymentResponse;
import roomescape.reservation.model.Reservation;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private String orderId;

    private Long amount;

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

}
