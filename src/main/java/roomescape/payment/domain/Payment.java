package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String paymentKey;

    @Column(nullable = false, length = 64)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    private String requestedAt;

    private String approvedAt;

    private static Payment of(final Long id, final String paymentKey, final String orderId,
                              final Long amount, final String requestedAt, final String approvedAt) {
        validate(paymentKey, orderId, amount);
        return new Payment(id, paymentKey, orderId, amount, requestedAt, approvedAt);
    }

    public static Payment withId(final Long id, final String paymentKey, final String orderId,
                                 final Long amount, final String requestedAt, final String approvedAt) {
        return of(id, paymentKey, orderId, amount, requestedAt, approvedAt);
    }

    public static Payment withoutId(final String paymentKey, final String orderId, final Long amount,
                                    final String requestedAt, final String approvedAt) {
        return of(null, paymentKey, orderId, amount, requestedAt, approvedAt);
    }

    private static void validate(
            final String paymentKey,
            final String orderId,
            final Long amount
    ) {
        Validator.of(Payment.class)
                .notNullField(Payment.Fields.paymentKey, paymentKey)
                .notNullField(Payment.Fields.orderId, orderId)
                .positiveNumberField(Payment.Fields.amount, amount);
    }
}
