package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.dto.response.TossPaymentResponse;
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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow();

        // 나중에 결제 실패 or 결제 취소는 지금 고려 x
        Payment payment = new Payment(response.orderId(), response.paymentKey(), response.approvedAt().toLocalDateTime(), response.totalAmount(), PaymentStatus.DONE, reservation);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void delete(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();
        payment.cancel();
    }
}
