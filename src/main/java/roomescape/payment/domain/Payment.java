package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private int amount;
    private String paymentKey;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @JoinColumn
    @OneToOne
    private Reservation reservation;

    public Payment(String orderId, int amount, String paymentKey, PaymentStatus status, Reservation reservation) {
        this(null, orderId, amount, paymentKey, status, reservation);
    }

    public void cancel() {
        reservation = null;
        status = PaymentStatus.CANCEL;
    }
}
