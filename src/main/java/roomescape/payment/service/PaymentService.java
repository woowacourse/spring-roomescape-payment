package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.dto.response.TossPaymentResponse;
import roomescape.reservation.domain.ReservationRepository;

@Service
public class PaymentService {

    private final TossPaymentClient paymentClient;
    private final PaymentTransactionService paymentTransactionService;

    public PaymentService(final ReservationRepository reservationRepository, final TossPaymentClient paymentClient, final PaymentTransactionService paymentTransactionService) {
        this.paymentClient = paymentClient;
        this.paymentTransactionService = paymentTransactionService;
    }

    public void confirmAndSavePayment(TossPaymentConfirmRequest request, long reservationId) {
        TossPaymentResponse tossPaymentResponse = paymentClient.confirmPayment(request);
        paymentTransactionService.savePayment(tossPaymentResponse);
        paymentTransactionService.confirmReservation(reservationId);
    }
}
