package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ErrorType;
import roomescape.exception.RoomescapeException;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;


    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void pay(PaymentRequest paymentRequest, long relationId) {
        PaymentResponse response = paymentClient.confirm(paymentRequest);
        paymentRepository.save(Payment.from(response.paymentKey(), response.method(), response.totalAmount(), relationId));
    }

    public void refund(long memberReservationId) {
        Payment payment = paymentRepository.findByRelatedId(memberReservationId)
                .orElseThrow((() -> new RoomescapeException(ErrorType.PAYMENT_NOT_FOUND)));

        paymentClient.cancel(payment.getPaymentKey());
        paymentRepository.delete(payment);
    }
}
