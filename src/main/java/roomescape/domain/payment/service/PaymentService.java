package roomescape.domain.payment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.dto.PaymentConfirmRequest;
import roomescape.domain.payment.dto.PaymentConfirmResponse;
import roomescape.domain.payment.entity.Payment;
import roomescape.domain.payment.exception.NotSupportedPaymentMethod;
import roomescape.domain.payment.repository.PaymentRepositoryInterface;
import roomescape.domain.reservation.entity.Reservation;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final List<PaymentProcessor> processors;
    private final PaymentRepositoryInterface paymentRepository;

    @Transactional
    public Payment processPayment(
            final PaymentType type,
            final PaymentConfirmRequest request,
            final Reservation reservation) {
        final PaymentProcessor paymentProcessor = getPaymentProcessor(type);
        final PaymentConfirmResponse response = paymentProcessor.processPayment(request);
        final Payment payment = new Payment(
                response.getPaymentKey(),
                response.getOrderId(),
                response.getTotalAmount(),
                reservation
        );
        final Payment savedPayment = paymentRepository.save(payment);

        return savedPayment;
    }

    @Transactional
    public void deleteByReservationId(final Long reservationId) {
        paymentRepository.deleteByReservationId(reservationId);
    }

    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    private PaymentProcessor getPaymentProcessor(final PaymentType type) {
        return processors.stream()
                .filter(processor -> processor.supports(type))
                .findFirst()
                .orElseThrow(() -> new NotSupportedPaymentMethod());
    }
}
