package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TossPaymentResponse approvePayment(final String orderId, final String paymentKey, final long amount) {
        return paymentClient.requestPaymentApprove(
                new TossPaymentRequest(orderId, paymentKey, amount));
    }

    @Transactional
    public void savePayment(final Reservation reservation, final TossPaymentResponse tossPaymentResponse) {
        paymentRepository.save(new Payment(reservation, tossPaymentResponse.orderId(), tossPaymentResponse.paymentKey(),
                tossPaymentResponse.totalAmount(), tossPaymentResponse.type()));
    }
}
