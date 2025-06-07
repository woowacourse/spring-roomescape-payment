package roomescape.payment.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.application.infrastructure.PaymentRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentType;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;
import roomescape.payment.presentation.dto.response.TossPaymentApproveResponse;
import roomescape.reservation.application.event.PaymentApprovedEvent;
import roomescape.reservation.application.event.PaymentFailedEvent;

@Service
@Slf4j
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository,
                          final ApplicationEventPublisher applicationEventPublisher) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public PaymentApproveResponse approvePayment(final PaymentApproveRequest request) {
        log.info("결제 승인 시도: orderId={}, amount={}, paymentKey={}",
                request.orderId(), request.amount(), request.paymentKey());
        Payment payment = new Payment(request.paymentKey(), request.orderId(), request.amount(), PaymentType.NORMAL);
        Payment savedPayment = paymentRepository.save(payment);

        try {
            TossPaymentApproveResponse tossPaymentApproveResponse = paymentClient.approvePayment(request);
            applicationEventPublisher.publishEvent(PaymentApprovedEvent.from(request, savedPayment));

            log.info("결제 승인 완료: paymentId={}, orderId={}, amount={}",
                    savedPayment.getId(), request.orderId(), request.amount());

            return PaymentApproveResponse.from(tossPaymentApproveResponse);
        } catch (PaymentException e) {
            applicationEventPublisher.publishEvent(PaymentFailedEvent.from(request, savedPayment));

            log.error("결제 승인 실패: orderId={}, amount={}, error={}",
                    request.orderId(), request.amount(), e.getMessage(), e);

            throw e;
        }
    }

    public Payment save(final Payment payment) {
        return paymentRepository.save(payment);
    }
}
