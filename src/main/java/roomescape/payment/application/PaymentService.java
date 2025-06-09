package roomescape.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.payment.application.dto.PaymentConfirmRequest;
import roomescape.payment.application.dto.PrePaymentValidRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment pend(final PaymentConfirmRequest request, final Reservation reservation) {
        final Payment payment = Payment.pending(
                request.orderId(),
                request.paymentKey(),
                request.amount(),
                reservation
        );
        return paymentRepository.save(payment);
    }

    @Transactional
    public void success(final Payment payment) {
        payment.success();
        paymentRepository.save(payment);
    }

    @Transactional
    public void fail(final Payment payment) {
        payment.fail();
        paymentRepository.save(payment);
    }

    @Transactional
    public Payment await(final PrePaymentValidRequest request, final Reservation reservation) {
        final Payment payment = Payment.await(request.orderId(), request.amount(), reservation);
        return paymentRepository.save(payment);
    }

    public void validatePrePayment(
            final PaymentConfirmRequest paymentConfirmRequest,
            final PrePaymentValidRequest prePaymentValidRequest
    ) {
        if (!prePaymentValidRequest.orderId().equals(paymentConfirmRequest.orderId())) {
            throw new BadRequestException("결제 주문번호가 일치하지 않습니다.");
        }
        if (!prePaymentValidRequest.amount().equals(paymentConfirmRequest.amount())) {
            throw new BadRequestException("결제 금액이 일치하지 않습니다.");
        }
    }
}
