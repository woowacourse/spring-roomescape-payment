package roomescape.external.tosspayment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.external.tosspayment.dto.PaymentConfirmRequest;
import roomescape.external.tosspayment.dto.TossPaymentConfirmCommand;

@Component
@RequiredArgsConstructor
public class TossPaymentAdapter {

    private final TossPaymentClient tossPaymentClient;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void confirmPayment(TossPaymentConfirmCommand command) {
        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(command.orderId(), command.amount(), command.paymentKey());
        tossPaymentClient.confirm(paymentRequest);
    }
}
