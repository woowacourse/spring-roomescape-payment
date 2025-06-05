package roomescape.payment.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;
import roomescape.payment.exception.PaymentMismatchException;

@Getter
@Embeddable
@FieldNameConstants
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentInfo {

    private String paymentKey;

    private int totalAmount;

    public PaymentInfo(final String paymentKey, final int totalAmount) {
        validate(paymentKey, totalAmount);
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
    }

    public void checkPaymentInfoMatch(final String paymentKey, final int totalAmount) {
        if ((!this.paymentKey.equals(paymentKey)) || (this.totalAmount != totalAmount)) {
            throw new PaymentMismatchException(DomainTerm.PAYMENT_AMOUNT, paymentKey, totalAmount);
        }
    }

    private static void validate(final String paymentKey, final int totalAmount) {
        Validator.of(PaymentInfo.class)
                .validateNotNull(Fields.paymentKey, paymentKey, DomainTerm.PAYMENT_KEY.label())
                .validateNotNull(Fields.totalAmount, totalAmount, DomainTerm.PAYMENT_AMOUNT.label());
    }
}
