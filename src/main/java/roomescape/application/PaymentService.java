package roomescape.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.exception.PaymentFailedException;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentProvider paymentProvider;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void confirm(final long reservationId, final String paymentKey, final String orderId, final int amount) {
        log.info("예약에 대한 결제 승인 요청을 받았습니다. reservationId = {}, paymentKey = {}", reservationId, paymentKey);
        var reservation = reservationRepository.getById(reservationId);
        if (!reservation.isPending()) {
            throw PaymentFailedException.byClient("보류중인 예약만 결제할 수 있습니다.");
        }

        var paymentRequest = new PaymentRequest(paymentKey, orderId, amount);
        confirmPayment(paymentRequest, reservation);
        log.info("예약에 대한 결제 승인(예약 확정)에 성공했습니다. reservation = {}", reservation);
    }

    private void confirmPayment(final PaymentRequest paymentRequest, final Reservation reservation) {
        try {
            var payment = paymentProvider.confirm(paymentRequest);
            reservation.confirm(payment);
        } catch (PaymentFailedException e) {
            reservationRepository.delete(reservation);
            log.warn("예약에 대한 결제 승인(예약 확정)에 실패했습니다. reservation = {}, payment request = {}", reservation, paymentRequest);
            log.warn("결제에 실패하여 보류중인 예약을 삭제합니다. reservationId = {}", reservation.id());
            throw e;
        }
    }
}
