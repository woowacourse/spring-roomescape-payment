package roomescape.payment.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import roomescape.logging.aspect.Loggable;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.service.ReservationPaymentService;

@Loggable
@Component
@RequiredArgsConstructor
public class ReservationPaymentEventListener {

    private final PaymentService paymentService;
    private final ReservationPaymentService reservationPaymentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentEvent(PaymentRequestedEvent event) {
        PaymentRequest paymentRequest = PaymentRequest.from(event.paymentInfoRequest());
        Long reservationId = event.reservationId();
        PaymentResponse response = paymentService.createOrder(paymentRequest);

        try {
            callTossPaymentApi(paymentRequest);
            reservationPaymentService.paid(reservationId, response.paymentKey());
        } catch (PaymentServerException | PaymentProcessException e) {
            reservationPaymentService.failedPayment(reservationId, response.paymentKey());
        }
    }

    private void callTossPaymentApi(PaymentRequest request) {
        PaymentResponse paymentConfirm = paymentService.confirmPayment(request);
        PaymentResponse payment = paymentService.getPayment(request.paymentKey());

        if (!paymentConfirm.orderId().equals(payment.orderId())) {
            throw new PaymentServerException("결제 서버에서 에러가 발생했습니다. 다시 시도해주세요.");
        }
    }
}
