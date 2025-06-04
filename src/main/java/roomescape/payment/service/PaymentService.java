package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.infrastructure.PaymentClient;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.payment.infrastructure.dto.reqeust.PaymentCommand;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment payAndCreatePayment(final PaymentRequest request, final Reservation reservation) {
        PaymentResponse paymentResponse = paymentClient.authPayment(PaymentCommand.createByPaymentRequest(request));
        Payment payment = Payment.createPaymentWithoutId(paymentResponse.paymentKey(), paymentResponse.orderId(),
                paymentResponse.totalAmount(), paymentResponse.getRequestedAt(), reservation);
        return paymentRepository.save(payment);
    }
}
