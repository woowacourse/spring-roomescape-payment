package roomescape.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.payment.application.dto.DefaultPaymentRequest;
import roomescape.payment.application.dto.PaymentConfirmRequest;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.payment.application.dto.PrePaymentRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public Payment pay(
            final PrePaymentRequest prePaymentRequest,
            final PaymentConfirmRequest paymentConfirmRequest,
            final Reservation reservation
    ) {
        validatePrePayment(paymentConfirmRequest, prePaymentRequest);

        final PaymentRequest paymentRequest = new DefaultPaymentRequest(
                paymentConfirmRequest.paymentKey(), paymentConfirmRequest.orderId(), paymentConfirmRequest.amount()
        );
        final Payment payment = Payment.pending(
                paymentConfirmRequest.orderId(),
                paymentConfirmRequest.paymentKey(),
                paymentConfirmRequest.amount(),
                reservation
        );
        paymentRepository.save(payment);
        
        try {
            final PaymentResponse paymentResponse = paymentClient.requestPayment(paymentRequest);
            payment.success();
        } catch (PaymentException e) {
            payment.fail();
            throw e;
        }
        return payment;
    }

    public Payment await(final PrePaymentRequest request, final Reservation reservation) {
        final Payment payment = Payment.await(request.orderId(), request.amount(), reservation);
        return paymentRepository.save(payment);
    }

    private void validatePrePayment(
            final PaymentConfirmRequest paymentConfirmRequest,
            final PrePaymentRequest prePaymentRequest
    ) {
        if (!prePaymentRequest.orderId().equals(paymentConfirmRequest.orderId())) {
            throw new BadRequestException("결제 주문번호가 일치하지 않습니다.");
        }
        if (!prePaymentRequest.amount().equals(paymentConfirmRequest.amount())) {
            throw new BadRequestException("결제 금액이 일치하지 않습니다.");
        }
    }
}
