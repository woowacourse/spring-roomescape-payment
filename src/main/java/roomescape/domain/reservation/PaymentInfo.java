package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "payment_info")
@Entity
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "pay_method")
    private String payMethod;

    @OneToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public PaymentInfo(String paymentKey, Long amount, String payMethod, Reservation reservation) {
        this(null, paymentKey, amount, payMethod, reservation);
    }

    public PaymentInfo(Long id, String paymentKey, Long amount, String payMethod, Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.payMethod = payMethod;
        this.reservation = reservation;
    }

    protected PaymentInfo() {
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

    public Reservation getReservation() {
        return reservation;
    }

    public String getPayMethod() {
        return payMethod;
    }
}
