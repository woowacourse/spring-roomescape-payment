package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.exception.PaymentRequestException;
import roomescape.payment.infrastructure.dto.PaymentRequest;
import roomescape.reservation.domain.Reservation;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(
        final PaymentClient paymentClient,
        final PaymentRepository paymentRepository
    ) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void confirmPayment(final PaymentRequest paymentRequest) {
        paymentClient.requestPayment(paymentRequest);
    }

    @Transactional
    public Payment savePayment(Reservation reservation, PaymentRequest paymentRequest) {
        Payment payment = Payment.createWithoutId(
            reservation,
            paymentRequest.paymentKey(),
            paymentRequest.orderId(),
            paymentRequest.amount());
        return paymentRepository.save(payment);
    }

    public Payment findByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
            .orElseThrow(() -> new PaymentRequestException("결제 정보를 찾을 수 없습니다."));
    }
}
