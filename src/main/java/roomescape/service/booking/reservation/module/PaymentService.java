package roomescape.service.booking.reservation.module;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.exception.RoomEscapeException;
import roomescape.infrastructure.payment.TossPaymentClient;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    public PaymentService(final PaymentRepository paymentRepository, TossPaymentClient tossPaymentClient) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentClient = tossPaymentClient;
    }

    public PaymentResponse payByToss(PaymentRequest paymentRequest) {
        return tossPaymentClient.confirm(paymentRequest);
    }

    public Payment save(final Payment rawPayment) {
        return paymentRepository.save(rawPayment);
    }

    public PaymentResponse findPaymentByReservation(final Reservation reservation) {
        Payment payment = findByReservation(reservation);
        return PaymentResponse.from(payment);
    }

    private Payment findByReservation(final Reservation reservation) {
        return paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new RoomEscapeException(
                        "예약정보와 일치하는 결제 정보가 존재하지 않습니다.",
                        "reservation_id : " + reservation.getId()
                ));
    }
}
