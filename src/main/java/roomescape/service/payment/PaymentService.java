package roomescape.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.response.PaymentSuccessResponse;
import roomescape.global.exception.roomescape.RoomEscapeErrorStatus;
import roomescape.global.exception.roomescape.RoomEscapeException;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentApproveClient paymentApproveClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void approveAndSave(String paymentKey, String orderId, int amount, Long reservationId) {
        final PaymentSuccessResponse response = paymentApproveClient.approvePayment(
                paymentKey,
                orderId,
                amount
        );
        paymentRepository.save(new Payment(reservationId, response.paymentKey(), response.totalAmount()));
    }

    public boolean isReservationPaid(Reservation reservation) {
        return paymentRepository.existsPaymentByReservationId(reservation.getId());
    }

    public Payment getPaymentByReservation(Reservation reservation) {
        return paymentRepository.findPaymentByReservationId(reservation.getId())
                .orElseThrow(() -> new RoomEscapeException(RoomEscapeErrorStatus.NON_EXIST_PAYMENT));
    }
}
