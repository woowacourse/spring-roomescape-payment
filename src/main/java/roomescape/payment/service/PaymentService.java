package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.model.Reservation;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient,
                          final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment confirm(final Reservation reservation, final ConfirmPaymentRequest confirmPaymentRequest) {
        Payment payment = paymentClient.confirm(confirmPaymentRequest);
        payment.assignReservation(reservation);
        return paymentRepository.save(payment);
    }
}
