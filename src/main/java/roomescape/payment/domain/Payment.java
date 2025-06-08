package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    PaymentKey paymentKey;

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false)
    Long reservationId;

    public Payment (PaymentKey paymentKey, BigDecimal amount, Long reservationId){
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservationId = reservationId;
    }
}
