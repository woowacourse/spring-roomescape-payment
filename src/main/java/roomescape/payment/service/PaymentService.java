package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.resonse.PaymentConfirmResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(TossPaymentClient tossPaymentClient, PaymentRepository paymentRepository) {
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment confirm(PaymentConfirmRequest confirmRequest, Reservation reservation) {
        PaymentConfirmResponse confirmResponse = tossPaymentClient.confirmPayment(confirmRequest);
        Payment payment = new Payment(
                confirmResponse.paymentKey(),
                confirmResponse.orderId(),
                confirmResponse.totalAmount(),
                reservation
        );

        return paymentRepository.save(payment);
    }
}
