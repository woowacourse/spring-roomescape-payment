package roomescape.business.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.exception.payment.PaymentExistsException;
import roomescape.exception.payment.PaymentNotFoundException;
import roomescape.infrastructure.PaymentRepository;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.dto.TossPaymentApproveRequest;
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
    public void approvePayment(Reservation reservation, String paymentKey, String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(PaymentNotFoundException::new);
        payment.approve(paymentKey, amount, reservation);
        paymentClient.approvePayment(new TossPaymentApproveRequest(paymentKey, orderId, amount));
    }

    @Transactional
    public String createPayment(PaymentRequest request) {
        if (paymentRepository.existsByOrderId(request.orderId())) {
            throw new PaymentExistsException();
        }
        Payment payment = paymentRepository.save(Payment.create(request.orderId(), request.amount()));
        return payment.getId().id();
    }
}
