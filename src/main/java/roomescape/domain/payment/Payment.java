package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import roomescape.domain.reservation.Reservation;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@EqualsAndHashCode(of = "id")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;
    private String orderId;
    private int amount;
    private LocalDateTime approvedAt;

    @OneToOne
    private Reservation reservation;


    public Payment(final Long id, final String paymentKey, final String orderId, final int amount,
                   final LocalDateTime approvedAt, final Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.approvedAt = approvedAt;
        this.reservation = reservation;
    }

    public static Payment createWithoutId(final String paymentKey,
                                          final String orderId,
                                          final int amount,
                                          final LocalDateTime approvedAt,
                                          final Reservation reservation
    ) {
        return new Payment(null, paymentKey, orderId, amount, approvedAt, reservation);
    }
}
