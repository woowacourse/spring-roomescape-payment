package roomescape.service.booking.reservation.module;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.infrastructure.payment.TossPaymentClient;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    public PaymentService(final PaymentRepository paymentRepository, TossPaymentClient tossPaymentClient) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentClient = tossPaymentClient;
    }

    public PaymentResponse payByToss(PaymentRequest paymentRequest) {
        return tossPaymentClient.confirm(paymentRequest);
    }

    public Payment save(final Payment rawPayment) {
        return paymentRepository.save(rawPayment);
    }

    public List<Payment> findPaymentByReservationIds(final List<Long> reservedReservationIds) {
        return paymentRepository.findByReservation_IdIn(reservedReservationIds);
    }
}
