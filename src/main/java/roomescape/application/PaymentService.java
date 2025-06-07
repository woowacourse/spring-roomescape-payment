package roomescape.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.exception.PaymentFailedException;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentProvider paymentProvider;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void pay(final long reservationId, final String paymentKey, final String orderId, final int amount) {
        log.info("예약에 대한 결제 승인 요청을 받았습니다. reservationId = {}, paymentKey = {}", reservationId, paymentKey);
        var reservation = reservationRepository.getById(reservationId);
        if (!reservation.isPending()) {
            throw PaymentFailedException.byClient("보류중인 예약만 결제할 수 있습니다.");
        }

        var request = new PaymentRequest(paymentKey, orderId, amount);
        var payment = paymentProvider.confirm(request);
        reservation.confirm(payment);
        log.info("예약에 대한 결제 승인(예약 확정)에 성공했습니다. reservation = {}", reservation);
    }
}
