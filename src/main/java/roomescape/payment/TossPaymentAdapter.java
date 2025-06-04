package roomescape.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.TossPaymentConfirmCommand;
import roomescape.payment.dto.TossPaymentConfirmResponse;

@Component
@RequiredArgsConstructor
public class TossPaymentAdapter {

    private final TossPaymentClient tossPaymentClient;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TossPaymentConfirmResponse confirmPayment(TossPaymentConfirmCommand command) {
        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(command.orderId(), command.amount(), command.paymentKey());
        return tossPaymentClient.confirm(paymentRequest);
    }
}
