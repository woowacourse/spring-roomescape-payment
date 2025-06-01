package roomescape.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.dto.PaymentConfirmRequest;

@Component
@AllArgsConstructor
public class TossPaymentAdapter {

    private final TossPaymentClient tossPaymentClient;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void confirmPayment(final String orderId, final Long amount, final String paymentKey) {
        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(orderId, amount, paymentKey);
        tossPaymentClient.confirm(paymentRequest);
    }
}
