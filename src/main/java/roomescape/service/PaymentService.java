package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.exception.NotFoundException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void createPayment(PaymentConfirmResponse paymentConfirm, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(""));
        Payment payment = Payment.createWithoutId(paymentConfirm.orderId(), paymentConfirm.paymentKey(),
                paymentConfirm.totalAmount(), reservation);
        paymentRepository.save(payment);
    }
}
