package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.client.dto.response.TossPaymentResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.infrastructure.JpaPaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;

@Service
public class PaymentService {

    private final JpaPaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(final JpaPaymentRepository paymentRepository, final ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Payment save(final TossPaymentResponse response, final long reservationId) {
        Reservation byId = reservationRepository.findById(reservationId)
                .orElseThrow();

        Payment payment = new Payment(response.orderId(), response.approvedAt().toLocalDateTime(), response.totalAmount(), PaymentStatus.DONE, byId);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void delete(final long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();
        payment.cancel();
    }
}
