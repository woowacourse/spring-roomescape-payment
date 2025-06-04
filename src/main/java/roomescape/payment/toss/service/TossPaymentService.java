package roomescape.payment.toss.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.toss.domain.TossPayment;
import roomescape.payment.toss.dto.TossPaymentRequest;
import roomescape.payment.toss.repository.TossPaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentRepository tossPaymentRepository;

    public void confirmPayment(Reservation reservation, TossPaymentRequest paymentRequest) {
        if (tossPaymentRepository.existsByPaymentKeyAndOrderId(paymentRequest.paymentKey(), paymentRequest.orderId())) {
            return;
        }

        tossPaymentRepository.save(new TossPayment(reservation, paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount()));
        tossPaymentClient.getPaymentConfirm(paymentRequest);
    }
}
