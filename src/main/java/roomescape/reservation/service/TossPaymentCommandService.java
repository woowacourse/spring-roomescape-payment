package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentKey;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.TossPayment;
import roomescape.reservation.external.toss.TossApiClient;
import roomescape.reservation.external.toss.TossPaymentRequest;
import roomescape.reservation.external.toss.TossPaymentResponse;
import roomescape.reservation.repository.TossPaymentRepository;

@Service
public class TossPaymentCommandService {
    private final TossPaymentRepository tossPaymentRepository;
    private final TossApiClient tossApiClient;

    public TossPaymentCommandService(final TossPaymentRepository tossPaymentRepository,
                                     final TossApiClient tossApiClient) {
        this.tossPaymentRepository = tossPaymentRepository;
        this.tossApiClient = tossApiClient;
    }

    public void createTossPayment(final TossPaymentRequest tossPaymentRequest, Reservation reservation) {
        TossPaymentResponse tossPaymentResponse = tossApiClient.requestPayment(tossPaymentRequest);
        tossPaymentRepository.save(new TossPayment(
                new OrderId(tossPaymentResponse.orderId()),
                new Amount(tossPaymentResponse.totalAmount()),
                new PaymentKey(tossPaymentResponse.paymentKey()),
                reservation
        ));
    }
}
