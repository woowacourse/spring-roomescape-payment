package roomescape.reservation.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String paymentAmount;

    public Payment() {
    }

    public Payment(final String paymentKey, final String paymentAmount) {
        this.paymentKey = paymentKey;
        this.paymentAmount = paymentAmount;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }
}
