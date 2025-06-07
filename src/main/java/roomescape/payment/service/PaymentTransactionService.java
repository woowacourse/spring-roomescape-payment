package roomescape.payment.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.response.TossPaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;

@Service
@Transactional
public class PaymentTransactionService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public PaymentTransactionService(final ReservationRepository reservationRepository, final PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public void savePayment(final TossPaymentResponse response) {
        Payment payment = new Payment(response.orderId(), response.approvedAt().toLocalDateTime(), response.totalAmount(), PaymentStatus.DONE, response.paymentKey());
        paymentRepository.save(payment);
    }

    public void confirmReservation(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다. id : " + reservationId));
        reservation.changePendingToPaid();
    }
}
