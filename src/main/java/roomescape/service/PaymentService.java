package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.repository.PaymentRepository;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.response.PaymentResponse;
import roomescape.exception.PaymentNotFoundException;
import roomescape.infrastructure.payment.PaymentClient;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse approve(PaymentRequest paymentRequest) {
        return paymentClient.approve(paymentRequest);
    }

    public Payment createPaymentWithReservation(PaymentResponse paymentResponse, Reservation reservation) {
        return Payment.createPaymentWithoutId(
                paymentResponse.orderId(),
                reservation,
                paymentResponse.paymentKey(),
                paymentResponse.totalAmount());
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment findPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(PaymentNotFoundException::new);
    }
}
