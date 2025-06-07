package roomescape.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import roomescape.application.PaymentService;
import roomescape.infrastructure.payment.PaymentClient;

@Component
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final PaymentClient paymentClient;

    public PaymentEventListener(PaymentService paymentService, PaymentClient paymentClient) {
        this.paymentService = paymentService;
        this.paymentClient = paymentClient;
    }

    @Async
    @EventListener
    public void processPayment(PaymentRequestedEvent event) {
        try {
            paymentClient.confirmPayment(event.getPaymentInfo());
            paymentService.completePayment(event.getPaymentId());
        } catch (Exception e) {
            paymentService.rejectPayment(event.getPaymentId());
        }
    }
}
