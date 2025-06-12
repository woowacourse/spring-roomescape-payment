package roomescape.payment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.util.DateTimeParser;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;
import roomescape.reservation.presentation.dto.ReservationRequest;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, final PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public Payment processPaymentRequest(final ReservationRequest reservationRequest) {
        TossPaymentResponse tossPaymentResponse = paymentClient.approve(reservationRequest);
        return paymentRepository.save(new Payment(
                tossPaymentResponse.getOrderId(),
                tossPaymentResponse.getPaymentKey(),
                tossPaymentResponse.getTotalAmount(),
                DateTimeParser.parse(tossPaymentResponse.getApprovedAt())
        ));
    }
}
