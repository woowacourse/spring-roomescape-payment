package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.logging.LogExecution;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.dto.response.TossPaymentResponse;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TossPaymentClient paymentClient;
    private final PaymentTransactionService paymentTransactionService;

    @LogExecution
    public void confirmAndSavePayment(TossPaymentConfirmRequest request, long reservationId) {
        TossPaymentResponse tossPaymentResponse = paymentClient.confirmPayment(request);
        Payment payment = paymentTransactionService.savePayment(tossPaymentResponse);
        paymentTransactionService.confirmReservation(reservationId, payment);
    }
}
