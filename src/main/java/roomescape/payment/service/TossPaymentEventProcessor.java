package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentEventProcessor implements PaymentEventProcessor {
    private final PaymentRepository paymentRepository;
    private final PaymentCancellationService paymentCancellationService;

    @Override
    public void saveNotPaidPayment(Reservation reservation) {
        Payment payment = Payment.builder()
                .member(reservation.getMember())
                .reservation(reservation)
                .status(PaymentStatus.NOT_PAID)
                .build();
        paymentRepository.save(payment);

        log.info("NOT_PAID 결제 저장 - reservationId={}, memberId={}",
                reservation.getId(), reservation.getMember().getId());
    }

    @Override
    public void cancelPayment(Long reservationId) {
        paymentCancellationService.cancelPayment(reservationId);
    }
}
