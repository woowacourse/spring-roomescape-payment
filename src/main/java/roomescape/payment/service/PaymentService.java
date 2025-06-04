package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.client.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.client.dto.response.TossPaymentResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.infrastructure.JpaPaymentRepository;
import roomescape.reservation.domain.ReservationRepository;

@Service
public class PaymentService {

    private final JpaPaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final TossPaymentClient paymentClient;

    public PaymentService(final JpaPaymentRepository paymentRepository, final ReservationRepository reservationRepository, final TossPaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public TossPaymentResponse confirm(TossPaymentConfirmRequest request) {
        return paymentClient.confirmPayment(request);
    }

    @Transactional
    public Payment save(final TossPaymentResponse response) {
        Payment payment = new Payment(response.orderId(), response.approvedAt().toLocalDateTime(), response.totalAmount(), PaymentStatus.DONE);
        return paymentRepository.save(payment);
    }
}
