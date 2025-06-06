package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.TossPaymentCancelRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentEventProcessor implements PaymentEventProcessor {
    private final TossRestClient tossRestClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

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

    @Transactional
    @Override
    public void cancelPayment(Long reservationId) {
        validateReservationExists(reservationId);
        Payment payment = getPaymentByReservationId(reservationId);

        TossPaymentCancelRequest cancelRequest = new TossPaymentCancelRequest("모종의 이유");
        TossPaymentResponse cancelResponse = tossRestClient.cancel(payment.getPaymentKey(), cancelRequest);
        validateCancelSuccess(cancelResponse, payment);
        payment.updateStatusTo(PaymentStatus.REFUNDED);

        log.info("결제 취소 성공 - paymentKey={}", payment.getPaymentKey());
    }

    private void validateReservationExists(Long reservationId) {
        if (reservationRepository.existsById(reservationId)) {
            return;
        }
        throw new NotFoundException("존재하지 않는 예약입니다, id: " + reservationId);
    }

    private void validateCancelSuccess(TossPaymentResponse cancelResponse, Payment payment) {
        if (cancelResponse.status().equals("CANCELED")) {
            return;
        }
        log.warn("결제 취소 실패 - status={}, paymentKey={}",
                cancelResponse.status(), payment.getPaymentKey());
        throw new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 취소 실패", true);
    }

    private Payment getPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        "reservationId에 해당하는 결제를 찾을 수 없습니다, reservationId: " + reservationId));
    }
}
