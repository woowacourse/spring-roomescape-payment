package roomescape.payment.toss.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.toss.domain.TossPayment;
import roomescape.payment.toss.dto.TossPaymentRequest;
import roomescape.payment.toss.dto.TossPaymentResponse;
import roomescape.payment.toss.repository.TossPaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentRepository tossPaymentRepository;

    public TossPayment savePayment(final Reservation reservation, final TossPaymentRequest request) {
        return tossPaymentRepository.save(new TossPayment(reservation, request.paymentKey(), request.orderId(), request.amount()));
    }

    public TossPaymentResponse confirmPayment(final TossPayment tossPayment) {
        return tossPaymentClient.getPaymentConfirm(TossPaymentRequest.from(tossPayment));
    }

    public TossPayment findByReservation(Reservation reservation) {
        return tossPaymentRepository.findByReservation_Id(reservation.getId());
    }
}
