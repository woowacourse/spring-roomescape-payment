package roomescape.reservationpayment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.booking.reservation.Reservation;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReservationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String paymentKey;

    private Long amount;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public ReservationPayment(final String paymentKey, final Long amount, final Reservation reservation) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }
}
