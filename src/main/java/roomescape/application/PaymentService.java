package roomescape.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.OrderId;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentKey;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.TransactionStatus;
import roomescape.domain.payment.TransactionStatusCode;
import roomescape.exception.PaymentFailedException;
import roomescape.exception.PaymentInternalException;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentProvider paymentProvider;
    private final PaymentRepository paymentRepository;

    public Payment register(String paymentKey, String orderId, long totalAmount, String status, long reservationId) {
        var payment = new Payment(new PaymentKey(paymentKey), new OrderId(orderId), totalAmount, PaymentStatus.valueOf(status), reservationId);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void pay(final String paymentKey, final String orderId, final long amount, final long reservationId) {
        var request = new PaymentRequest(paymentKey, orderId, amount);

        var paymentDetails = paymentProvider.confirm(request);
        if (paymentDetails.isFailed()) {
            throwPaymentException(paymentDetails.status());
        }

        var confirmation = paymentDetails.confirmation();
        register(confirmation.paymentKey(), confirmation.orderId(), confirmation.totalAmount(), confirmation.status(), reservationId);
    }

    private void throwPaymentException(final TransactionStatus status) {
        if (TransactionStatusCode.FAILED_PAYMENT.equals(status.code())) {
            throw new PaymentFailedException(status.message());
        }
        throw new PaymentInternalException(status.message());
    }
}
