package roomescape.payment;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import roomescape.LoggerUtil;

@Service
public class PaymentService {
    private final TossPaymentClient tossPaymentClient;
    private static final Logger logger = LoggerUtil.getLogger(PaymentService.class);

    public PaymentService(TossPaymentClient tossPaymentClient) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public void pay(PaymentConfirmRequest paymentConfirmRequest) {
        PaymentConfirmResponse paymentConfirmResponse = tossPaymentClient.requestPayment(paymentConfirmRequest);
    }
}
