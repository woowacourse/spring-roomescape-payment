package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.request.payment.PaymentRequest;
import roomescape.dto.response.payment.PaymentResponse;
import roomescape.infrastructure.payment.PaymentClient;

@Service
public class TossPaymentService implements PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public TossPaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest, Reservation reservation) {
        PaymentResponse paymentResponse = paymentClient.pay(paymentRequest);
        Payment payment = paymentResponse.toEntity(reservation);
        Payment savedPayment = paymentRepository.save(payment);
        return new PaymentResponse(savedPayment.getPaymentKey(), savedPayment.getOrderId(), savedPayment.getTotalAmount());
    }
}
