package roomescape.application.payment;

import roomescape.domain.payment.AdminPayment;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.TossPayment;

public record PaymentResult(
        PaymentType paymentType,
        String paymentKey,
        Long amount
) {

    public static PaymentResult from(final TossPayment tossPayment) {
        return new PaymentResult(
                PaymentType.TOSS,
                tossPayment.getPaymentKey(),
                tossPayment.getAmount()
        );
    }

    public static PaymentResult from(final AdminPayment adminPayment) {
        return new PaymentResult(
                PaymentType.ADMIN,
                String.valueOf(adminPayment.getAdminId()),
                0L
        );
    }

}
