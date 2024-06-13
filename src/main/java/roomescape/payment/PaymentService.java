package roomescape.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.reservation.dto.CreateReservationRequest;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.payment.dto.CancelPaymentRequest;
import roomescape.payment.dto.CreatePaymentRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.repository.PaymentRepository;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void savePayment(final CreateReservationRequest request, final Reservation savedReservation) {
        final PaymentConfirmResponse paymentConfirmResponse = paymentClient.postPayment(
                new CreatePaymentRequest(request.paymentKey(), request.orderId(), request.amount()));
        final Payment payment = paymentConfirmResponse.toPayment(savedReservation);
        paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(final long reservationId) {
        final Optional<Payment> findPayment = paymentRepository.findByReservationId(reservationId);
        if (findPayment.isEmpty()) {
            return;
        }
        final Payment payment = findPayment.get();
        paymentClient.cancelPayment(new CancelPaymentRequest(payment.getPaymentKey(), "단순변심"));
        paymentRepository.delete(payment);
    }
}
