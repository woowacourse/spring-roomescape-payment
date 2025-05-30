package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.client.dto.response.TossPaymentResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Payment save(TossPaymentResponse response, long reservationId) {
        Reservation byId = reservationRepository.findById(reservationId)
                .orElseThrow();

        Payment payment = new Payment(response.orderId(), response.paymentKey(), response.approvedAt().toLocalDateTime(), response.totalAmount(), PaymentStatus.DONE, byId);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void delete(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();
        payment.cancel();
    }
}
