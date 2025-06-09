package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.external.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TossPaymentService.class);

    private final TossRestClient tossApiClient;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDto approve(PaymentRequestDto request, Reservation reservation) {
        PaymentResponseDto paymentResponseDto = tossApiClient.confirmPayment(request);
        Payment payment = Payment.of(paymentResponseDto, reservation);
        paymentRepository.save(payment);
        return paymentResponseDto;
    }

    @Override
    public void cancelPaymentByReservation(Reservation reservation) {
        paymentRepository.findByReservation(reservation)
                .ifPresentOrElse(
                        payment -> {
                            payment.changeStatus(PaymentStatus.CANCELED);
                            LOGGER.info("[결제 취소] 예약 ID={}에 대한 결제 ID={} 취소됨", reservation.getId(), payment.getId());
                        },
                        () -> LOGGER.warn("[결제 취소 실패] 예약 ID={}에 대한 결제를 찾을 수 없음", reservation.getId())
                );
    }
}
