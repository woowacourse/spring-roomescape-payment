package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants
@EqualsAndHashCode
@ToString
@Embeddable
public class PaymentAmount {

    @Column(name = "amount")
    private int value;

    public static PaymentAmount from(final int amount) {
        validate(amount);
        return new PaymentAmount(amount);
    }

    private static void validate(final int value) {
        Validator.of(PaymentAmount.class)
                .validatePositive(Fields.value, value, DomainTerm.PAYMENT_AMOUNT.label());
    }

}
