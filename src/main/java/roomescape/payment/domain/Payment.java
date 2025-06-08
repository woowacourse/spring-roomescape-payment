package roomescape.payment.domain;

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

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private Long amount;

    private static Payment of(final Long id, final String paymentKey, final Long amount) {
        validate(paymentKey, amount);
        return new Payment(id, paymentKey, amount);
    }

    public static Payment withId(final Long id, final String paymentKey, final Long amount) {
        return of(id, paymentKey, amount);
    }

    public static Payment withoutId(final String paymentKey, final Long amount) {
        return of(null, paymentKey, amount);
    }

    private static void validate(final String paymentKey, final Long amount) {
        Validator.of(Payment.class)
            .notNullField(Payment.Fields.paymentKey, paymentKey)
            .notNullField(Payment.Fields.amount, amount);
    }
}
