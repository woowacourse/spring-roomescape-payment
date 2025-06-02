package roomescape.payment.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.application.infrastructure.PaymentRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.exception.PaymentApproveException;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;
import roomescape.reservation.application.event.PaymentApprovedEvent;
import roomescape.reservation.application.event.PaymentFailedEvent;

@Service
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

    public PaymentApproveResponse approvePayment(final PaymentApproveRequest paymentApproveRequest) {
        try {
            PaymentApproveResponse paymentApproveResponse = paymentClient.approvePayment(paymentApproveRequest);
            applicationEventPublisher.publishEvent(PaymentApprovedEvent.from(paymentApproveRequest));
            return paymentApproveResponse;
        } catch (PaymentApproveException e) {
            applicationEventPublisher.publishEvent(PaymentFailedEvent.from(paymentApproveRequest));
            throw e;
        }
    }

    public Payment save(final Payment payment) {
        return paymentRepository.save(payment);
    }
}
