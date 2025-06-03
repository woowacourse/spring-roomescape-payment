package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    @Builder
    private Payment(final Long id, @NonNull final String orderId, @NonNull String paymentKey, @NonNull Long amount) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public static Payment from(final String orderId, final String paymentKey, final Long amount) {
        return builder()
                .orderId(orderId)
                .paymentKey(paymentKey)
                .amount(amount)
                .build();
    }
}
