package roomescape.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.domain.PaymentClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest paymentRequest) {
        return paymentClient.confirmPayment(paymentRequest);
    }

    public void confirmPayment(ConfirmPaymentRequest paymentRequest, Reservation reservation) {
        ConfirmPaymentResponse response = paymentClient.confirmPayment(paymentRequest);
        Payment payment = new Payment(response.orderId(),
                response.paymentKey(),
                response.totalAmount(),
                reservation);
        reservation.payForReservation(payment);
        paymentRepository.save(payment);
    }

    public void refundReservation(Long reservationId) {
        Optional<Payment> paymentOptional = paymentRepository.findFetchByReservationId(reservationId);

        if (paymentOptional.isEmpty()) {
            return;
        }

        Payment payment = paymentOptional.get();
        paymentClient.refund(payment);
    }
}
