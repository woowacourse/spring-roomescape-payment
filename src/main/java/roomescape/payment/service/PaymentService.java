package roomescape.payment.service;

import java.util.List;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.controller.dto.response.PaymentResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.dto.request.PaymentConfirmRequest;
import roomescape.payment.service.dto.resonse.PaymentConfirmResponse;
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
        return new Payment(
                confirmResponse.paymentKey(),
                confirmResponse.orderId(),
                confirmResponse.totalAmount(),
                reservation
        );
    }

    @Retryable(maxAttempts = 2)
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Transactional
    public void deleteByReservationId(Long reservationId) {
        paymentRepository.deleteByReservationId(reservationId);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentResponse::toResponse)
                .toList();
    }
}
