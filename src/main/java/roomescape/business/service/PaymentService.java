package roomescape.business.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.Payment;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.DuplicatedException;
import roomescape.infrastructure.PaymentRepository;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.presentation.dto.request.PaymentRequest;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public String createPayment(PaymentRequest request) {
        if (paymentRepository.existsByOrderId(request.orderId())) {
            throw new DuplicatedException(ErrorCode.RESERVATION_DUPLICATED);
        }
        Payment payment = paymentRepository.save(Payment.create(request.orderId(), request.amount()));
        return payment.getId().id();
    }
}
