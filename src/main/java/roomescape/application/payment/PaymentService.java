package roomescape.application.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;

@Component
public class PaymentService {
    private final PaymentClient paymentClient;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public PaymentService(PaymentClient paymentClient,
                          ReservationPaymentRepository reservationPaymentRepository) {
        this.paymentClient = paymentClient;
        this.reservationPaymentRepository = reservationPaymentRepository;
    }

    @Transactional
    public void purchase(Long reservationId, PaymentRequest request) {
        ReservationPayment reservationPayment = new ReservationPayment(
                request.orderId(), reservationId, request.paymentKey(), request.amount()
        );
        reservationPaymentRepository.save(reservationPayment);
        paymentClient.requestPurchase(request);
    }
}
