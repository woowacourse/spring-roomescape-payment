package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.exception.PaymentNotFoundException;
import roomescape.payment.infrastructure.PaymentClient;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.payment.infrastructure.dto.reqeust.PaymentCommand;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse sendPaymentRequest(final PaymentRequest request) {
        return paymentClient.authPayment(PaymentCommand.createByPaymentRequest(request));
    }

    public Payment createPayment(final PaymentRequest request, final Reservation reservation) {
        Payment payment = Payment.createPendingPaymentWithoutId(request.paymentKey(), request.orderId(),
                request.amount(), reservation);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void updatePaymentToFail(final Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("요청한 id와 일치하는 결제 정보가 없습니다."));
        payment.changeToFail();
    }

    @Transactional
    public void updatePaymentToSuccess(final Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("요청한 id와 일치하는 결제 정보가 없습니다."));
        payment.changeToSuccess();
    }

}
