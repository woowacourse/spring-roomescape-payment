package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.event.PaymentRequestedEvent;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.exception.NotFoundException;
import roomescape.infrastructure.payment.PaymentClient;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void requestPayment(final Reserved reserved, final PaymentInfo paymentInfo) {
        Payment payment = paymentRepository.save(
                Payment.register(
                        paymentInfo.paymentKey(),
                        paymentInfo.orderId(),
                        paymentInfo.orderName(),
                        paymentInfo.amount()
                )
        );
        reserved.registerPayment(payment);

        eventPublisher.publishEvent(new PaymentRequestedEvent(this, payment.getId(), paymentInfo));
    }

    @Transactional
    public void completePayment(Long paymentId) {
        getPaymentById(paymentId).completePayment();
    }

    @Transactional
    public void rejectPayment(Long paymentId) {
        getPaymentById(paymentId).rejectPayment();
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 결제 정보입니다."));
    }
}
