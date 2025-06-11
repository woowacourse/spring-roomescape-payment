package roomescape.unit.business.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.business.service.PaymentApproveService;
import roomescape.business.service.PaymentService;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.dto.TossPaymentApproveRequest;
import roomescape.presentation.api.PaymentApproveRequest;

class PaymentApproveServiceTest {

    private final PaymentApproveService paymentApproveService;
    private final PaymentClient paymentClient;
    private final PaymentService paymentService;

    public PaymentApproveServiceTest() {
        this.paymentClient = mock(PaymentClient.class);
        this.paymentService = mock(PaymentService.class);
        this.paymentApproveService = new PaymentApproveService(paymentService, paymentClient);
    }

    @Test
    void 결제를_승인하고_예약을_확정한다() {
        // given
        String paymentId = "paymentId1";
        String paymentKey = "paymentKey1";
        Long amount = 1000L;

        Reservation reservation = Reservation.restore("reservationId1", null, null, null, null);
        Payment payment = Payment.restore(paymentId, null, amount, PaymentStatus.IN_PROGRESS, reservation);

        // when
        paymentApproveService.requestApproveAndCompletePayment(paymentId,
                new PaymentApproveRequest(paymentKey, amount));

        // then
        verify(paymentClient).approvePayment(new TossPaymentApproveRequest(paymentKey, paymentId, amount));
        InOrder inOrder = inOrder(paymentService, paymentClient);
        inOrder.verify(paymentService).completePayment(any(), any(), any());
        inOrder.verify(paymentClient).approvePayment(any());
    }
}