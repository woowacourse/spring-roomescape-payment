package roomescape.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentStatusCode;
import roomescape.exception.PaymentFailedException;
import roomescape.exception.PaymentInternalException;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentProvider paymentProvider;

    public void pay(final String paymentKey, final String orderId, final long amount) {
        var request = new PaymentRequest(paymentKey, orderId, amount);

        var paymentDetails = paymentProvider.confirm(request);
        if (paymentDetails.isFailed()) {
            throwPaymentException(paymentDetails.status());
        }
    }

    private static void throwPaymentException(final PaymentStatus status) {
        if (PaymentStatusCode.FAILED_PAYMENT.equals(status.code())) {
            throw new PaymentFailedException(status.message());
        }
        throw new PaymentInternalException(status.message());
    }
}
