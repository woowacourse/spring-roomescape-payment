package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.util.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentReservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY,   optional = false)
    @JoinColumn(name = "reservation_id",  nullable = false)
    private Reservation reservation;

    @Builder
    public PaymentReservation(final Long id, @NonNull final Payment payment, @NonNull final Reservation reservation) {
        this.id = id;
        this.payment = payment;
        this.reservation = reservation;
    }

    public static PaymentReservation of(final Payment payment, final Reservation reservation) {
        return PaymentReservation.builder()
                .payment(payment)
                .reservation(reservation)
                .build();
    }
}
