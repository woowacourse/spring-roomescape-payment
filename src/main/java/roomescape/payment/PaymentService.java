package roomescape.payment;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Reservation;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public ReservationPaymentResponse savePayment(PaymentResponse paymentResponse, Reservation reservation) {
        Payment payment = new Payment(paymentResponse.orderId(), paymentResponse.paymentKey(),
                paymentResponse.totalAmount(), reservation, paymentResponse.approvedAt());
        Payment saved = paymentRepository.save(payment);
        return ReservationPaymentResponse.from(saved);
    }

    /**
     *
     * @param reservationId 예약 ID
     * @return 결제 취소를 위한 paymentKey
     */
    public String deletePaymentByReservationId(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomEscapeException(
                        ErrorType.PAYMENT_NOT_POUND, String.format("[reservationId: %d]", reservationId),
                        HttpStatus.NOT_FOUND));
        paymentRepository.delete(payment);
        return payment.getPaymentKey();
    }
}
