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
    String paymentKey;

    @Column(nullable = false)
    BigDecimal amount;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    Reservation reservation;

    public Payment (String paymentKey, BigDecimal amount, Reservation reservation){
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }
}
