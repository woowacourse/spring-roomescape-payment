package roomescape.service.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

@Service
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public Payment confirmPayment(PaymentConfirmInput paymentConfirmInput, Reservation reservation) {
        PaymentConfirmOutput paymentConfirmOutput = paymentClient.confirmPayment(paymentConfirmInput);
        Payment payment = paymentConfirmOutput.toPayment(reservation);
        return paymentRepository.save(payment);
    }
}
