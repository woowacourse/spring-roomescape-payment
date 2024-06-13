package roomescape.service.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(
        PaymentRepository paymentRepository,
        @Qualifier("paymentClientProxy") PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment approvePayment(PaymentRequest request, Reservation reservation) {
        Payment payment = paymentClient.approve(request);
        payment.complete(reservation);

        return paymentRepository.save(payment);
    }

    public void cancelPayment(Reservation reservation) {
        paymentRepository.findByReservationId(reservation.getId())
            .ifPresent(payment -> {
                paymentClient.cancel(payment.getPaymentKey());
                payment.cancel();
            });
    }
}
