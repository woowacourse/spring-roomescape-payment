package roomescape.payment.infrastructure.client.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentAmount;
import roomescape.reservation.domain.Reservation;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record PaymentResult(String paymentKey,
                            int totalAmount,
                            String orderId) {

    public PaymentResult {
        validate(paymentKey, orderId, totalAmount);
    }

    public boolean verifyPayment(PaymentRequest request, PaymentResult paymentResult) {
        return paymentResult.orderId().equals(request.orderId())
                && paymentResult.paymentKey().equals(request.paymentKey())
                && paymentResult.totalAmount() == request.amount();
    }

    private void validate(final String paymentKey, final String orderId, final int amount) {
        Validator.of(PaymentResult.class)
                .validateNotNull(Fields.paymentKey, paymentKey, DomainTerm.PAYMENT_KEY.label())
                .validateNotNull(Fields.orderId, orderId, DomainTerm.PAYMENT_ORDER_ID.label())
                .validateNotNull(Fields.totalAmount, amount, DomainTerm.PAYMENT_AMOUNT.label());
    }

    public Payment toEntity(final Reservation reservation) {
        return Payment.of(paymentKey, PaymentAmount.from(totalAmount), orderId, reservation);
    }
}
