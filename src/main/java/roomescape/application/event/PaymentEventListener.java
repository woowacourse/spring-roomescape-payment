package roomescape.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import roomescape.application.PaymentService;
import roomescape.infrastructure.payment.PaymentClient;

@Slf4j
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
            log.info("결제 승인 성공 - 결제ID: {}", event.getPaymentId());
            paymentService.completePayment(event.getPaymentId());
        } catch (Exception e) {
            log.error("결제 승인 실패 - 결제ID: {}, 오류 메시지: {}", event.getPaymentId(), e.getMessage(), e);
            paymentService.rejectPayment(event.getPaymentId());
        }
    }
}
