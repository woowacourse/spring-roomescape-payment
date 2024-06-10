package roomescape.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.payment.PaymentClient;
import roomescape.client.payment.dto.TossPaymentConfirmRequest;
import roomescape.client.payment.dto.TossPaymentConfirmResponse;
import roomescape.exception.PaymentConfirmException;
import roomescape.exception.model.PaymentConfirmExceptionCode;
import roomescape.registration.domain.reservation.domain.Reservation;

@Transactional
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment confirmPayment(TossPaymentConfirmRequest tossPaymentConfirmRequest, Reservation reservation) {
        TossPaymentConfirmResponse tossResponse = paymentClient.sendPaymentConfirmToToss(tossPaymentConfirmRequest);
        Payment payment = new Payment(tossResponse, reservation);

        return paymentRepository.save(payment);
    }

    public Payment findPaymentByReservationId(long reservationId) {
        return paymentRepository.findPaymentByReservationId(reservationId)
                .orElseThrow(() -> new PaymentConfirmException(PaymentConfirmExceptionCode.FOUND_PAYMENT_IS_NULL_EXCEPTION));
    }
}
