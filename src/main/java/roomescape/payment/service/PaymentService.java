package roomescape.payment.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.payment.client.TossPayRestClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.CancelRequest;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.dto.ReservationResponse;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPayRestClient tossPayRestClient;

    public PaymentService(PaymentRepository paymentRepository, TossPayRestClient tossPayRestClient) {
        this.paymentRepository = paymentRepository;
        this.tossPayRestClient = tossPayRestClient;
    }

    public PaymentResponse pay(PaymentRequest paymentRequest, Long reservationId) {
        Payment payment = tossPayRestClient.pay(paymentRequest);
        payment.bindToReservation(reservationId);
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.toResponse(savedPayment);
    }

    public void cancel(ReservationResponse canceledReservation) {
        Optional<Payment> paymentById = paymentRepository.findByReservationId(canceledReservation.id());
        paymentById.ifPresent(
                payment -> {
                        tossPayRestClient.cancel(new CancelRequest(payment.getPaymentKey()));
                        payment.cancel();
                        paymentRepository.save(payment);
                }
        );
    }
}
