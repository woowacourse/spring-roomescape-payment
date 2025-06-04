package roomescape.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.exception.PaymentFailedException;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentProvider paymentProvider;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void pay(final long reservationId, final String paymentKey, final String orderId, final long amount) {
        var reservation = reservationRepository.getById(reservationId);
        if (!reservation.isPending()) {
            throw PaymentFailedException.byClient("보류중인 예약만 결제할 수 있습니다.");
        }

        var request = new PaymentRequest(paymentKey, orderId, amount);
        var paymentConfirmation = paymentProvider.confirm(request);
        reservation.confirm(new Payment(paymentConfirmation.paymentKey(), paymentConfirmation.totalAmount()));
    }
}
