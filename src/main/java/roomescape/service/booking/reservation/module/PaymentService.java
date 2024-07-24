package roomescape.service.booking.reservation.module;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.dto.payment.CancelRequest;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.infrastructure.payment.toss.TossPaymentClient;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    public PaymentService(final PaymentRepository paymentRepository, TossPaymentClient tossPaymentClient) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentClient = tossPaymentClient;
    }

    public Payment save(final Payment rawPayment) {
        return paymentRepository.save(rawPayment);
    }

    public List<Payment> findPaymentByReservationIds(final List<Long> reservedReservationIds) {
        return paymentRepository.findByReservation_IdIn(reservedReservationIds);
    }

    public PaymentResponse cancelByToss(final CancelRequest cancelRequest, final PaymentResponse paymentResponse) {
        return tossPaymentClient.cancel(cancelRequest, paymentResponse);
    }

    public PaymentResponse payByToss(final UserReservationPaymentRequest userReservationPaymentRequest) {
        PaymentRequest paymentRequest = PaymentRequest.from(userReservationPaymentRequest);
        return tossPaymentClient.confirm(paymentRequest);
    }
}
