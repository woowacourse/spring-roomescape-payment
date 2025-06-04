package roomescape.payment.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "reservation_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    @Embedded
    private PaymentInfo paymentInfo;

    @Enumerated(value = EnumType.STRING)
    private PaymentGateway paymentGateway;

    protected Payment() {
    }

    public Payment(final Long id, final Reservation reservation, final PaymentInfo paymentInfo,
        final PaymentGateway paymentGateway) {
        this.id = id;
        this.reservation = reservation;
        this.paymentInfo = paymentInfo;
        this.paymentGateway = paymentGateway;
    }

    public Payment(final Reservation reservation, final PaymentInfo paymentInfo,
        final PaymentGateway paymentGateway) {
        this(null, reservation, paymentInfo, paymentGateway);
    }

    public Long getId() {
        return id;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment waiting = (Payment) o;
        if (id == null || waiting.id == null) {
            return false;
        }
        return Objects.equals(id, waiting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
