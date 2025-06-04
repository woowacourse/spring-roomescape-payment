package roomescape.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.service.TossPaymentService;
import roomescape.reservation.service.ReservationPaymentService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationPaymentEventListener {

    private final TossPaymentService tossPaymentService;
    private final ReservationPaymentService reservationPaymentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentEvent(TossPaymentRequestedEvent event) {
        TossPaymentRequest paymentRequest = TossPaymentRequest.from(event.paymentInfoRequest());
        Long reservationId = event.reservationId();
        TossPaymentResponse response = tossPaymentService.createOrder(paymentRequest);

        try {
            callTossPaymentApi(paymentRequest);
            reservationPaymentService.paid(reservationId, response.paymentKey());
        } catch (PaymentServerException | PaymentProcessException e) {
            log.error("Payment process failed for reservationId: {}, error: {}", reservationId, e.getMessage());
            reservationPaymentService.failedPayment(reservationId, response.paymentKey());
        }
    }

    private void callTossPaymentApi(TossPaymentRequest request) {
        TossPaymentResponse paymentConfirm = tossPaymentService.confirmPayment(request);
        TossPaymentResponse payment = tossPaymentService.getPayment(request.paymentKey());

        if (!paymentConfirm.orderId().equals(payment.orderId())) {
            throw new PaymentServerException("결제 서버에서 에러가 발생했습니다. 다시 시도해주세요.");
        }
    }
}
