package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.InvalidReservationException;
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

        // 결제 실패 or 결제 취소는 지금 고려 x
        Payment payment = new Payment(response.orderId(), response.paymentKey(), response.totalAmount(), PaymentStatus.DONE, reservation);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void confirm(Long paymentId) {
        Payment payment = getPayment(paymentId);
        payment.confirm();
    }

    @Transactional
    public void cancel(Long paymentId) {
        Payment payment = getPayment(paymentId);
        payment.cancel();
    }

    private Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new InvalidReservationException("존재하지 않는 결제입니다."));
    }
}
