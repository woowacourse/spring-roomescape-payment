package roomescape.domain.reservation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String paymentKey;

    @NotNull
    private String amount;

    @NotNull
    @OneToOne
    private Reservation reservation;

    public Payment(String paymentKey, String amount, Reservation reservation) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }

    protected Payment() {
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
