package roomescape.reservation.external.toss;

import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentKey;
import roomescape.reservation.domain.PaymentType;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.service.PaymentService;

@Service
public class TossPaymentService implements PaymentService<TossPaymentRequest> {
    private final PaymentRepository paymentRepository;
    private final TossApiClient tossApiClient;

    public TossPaymentService(final PaymentRepository paymentRepository,
                              final TossApiClient tossApiClient) {
        this.paymentRepository = paymentRepository;
        this.tossApiClient = tossApiClient;
    }

    @Override
    public void createPayment(final TossPaymentRequest request, final Reservation reservation) {
        TossPaymentResponse tossPaymentResponse = tossApiClient.requestPayment((TossPaymentRequest) request);
        paymentRepository.save(new Payment(
                new OrderId(tossPaymentResponse.orderId()),
                new Amount(tossPaymentResponse.totalAmount()),
                new PaymentKey(tossPaymentResponse.paymentKey()),
                reservation
        ));
    }

    @Override
    public PaymentType getType() {
        return PaymentType.TOSS;
    }
}
