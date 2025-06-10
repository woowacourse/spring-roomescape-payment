package roomescape.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.exception.PaymentFailedException;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentClient paymentClient;
    private final ReservationRepository reservationRepository;

    public Payment payReservation(final long reservationId, final PaymentRequest paymentRequest) {
        validateReservationPending(reservationId);
        try {
            var payment = paymentClient.requestPay(paymentRequest);
            log.info("예약 결제에 성공했습니다. 결제 Key = {}, 예약 ID = {}", paymentRequest.paymentKey(), reservationId);
            return payment;

        } catch (PaymentFailedException e) {
            reservationRepository.deleteByIdOrElseThrow(reservationId);
            log.warn("예약 결제에 실패했습니다. 결제 Key = {}, 예약 ID = {}", paymentRequest.paymentKey(), reservationId);
            throw e;
        }
    }

    private void validateReservationPending(final long reservationId) {
        var reservation = reservationRepository.getById(reservationId);
        if (!reservation.isPending()) {
            throw PaymentFailedException.byClient("보류중인 예약만 결제할 수 있습니다.");
        }
    }
}
