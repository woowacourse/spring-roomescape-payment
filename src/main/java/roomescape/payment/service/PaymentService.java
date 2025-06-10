package roomescape.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.InvalidReservationException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.domain.PaymentStatus;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment save(TossPaymentResponse response) {
        // 결제 실패 or 결제 취소는 지금 고려 x
        Payment payment = new Payment(response.orderId(), response.paymentKey(), response.totalAmount(), PaymentStatus.DONE);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void confirm(Long paymentId) {
        Payment payment = getPayment(paymentId);
        payment.confirm();
    }

    @Transactional
    public void cancel(Long paymentId) {
        Payment payment = getPayment(paymentId);
        payment.cancel();
    }

    private Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> {
            log.warn("결제 조회 실패 - 존재하지 않는 결제 paymentId: {}", paymentId);
            return new InvalidReservationException("존재하지 않는 결제입니다.");
        });
    }
}
