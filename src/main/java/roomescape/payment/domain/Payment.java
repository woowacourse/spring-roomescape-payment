package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

@Entity
@NoArgsConstructor
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private String orderId;

    private String paymentKey;

    private long amount;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    public Payment(Reservation reservation, String orderId, String paymentKey, long amount, PaymentType type) {
        this.reservation = reservation;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.type = type;
    }
}
