package roomescape.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.exception.PaymentException;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentProvider paymentProvider;

    public void pay(final String paymentKey, final String orderId, final long amount) {
        var request = new PaymentRequest(paymentKey, orderId, amount);

        var paymentDetails = paymentProvider.confirm(request);
        if (paymentDetails.isFailed()) {
            var status = paymentDetails.status();
            throw new PaymentException(status.message());
        }
    }
}
