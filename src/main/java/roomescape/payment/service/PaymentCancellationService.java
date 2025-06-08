package roomescape.payment.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.TossPaymentCancelRequest;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancellationService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final TossRestClient tossRestClient;

    public void cancelPayment(Long reservationId) {
        validateReservationExists(reservationId);

        Optional<Payment> findPayment = paymentRepository.findByReservationId(reservationId);
        if (notExistsPayment(reservationId, findPayment)) {
            return;
        }

        Payment payment = findPayment.get();
        validateCanCancellationStatus(reservationId, payment);

        processCancellation(payment);
    }

    private void validateReservationExists(Long reservationId) {
        if (reservationRepository.existsById(reservationId)) {
            return;
        }
        throw new NotFoundException("존재하지 않는 예약입니다, id: " + reservationId);
    }

    private boolean notExistsPayment(Long reservationId, Optional<Payment> findPayment) {
        if(findPayment.isEmpty()) {
            log.info("결제 정보가 없는 예약 - reservationId={}", reservationId);
            return true;
        }
        return false;
    }

    private void validateCanCancellationStatus(Long reservationId, Payment payment) {
        if(payment.getStatus() != PaymentStatus.COMPLETED) {
            log.info("결제 완료 정보가 없어 결제 취소 불가 - reservationId={}, paymentKey={}", reservationId, payment.getPaymentKey());
            throw new TossPaymentException(HttpStatus.BAD_REQUEST, "결제 취소할 수 있는 상태가 아닙니다.", false);
        }
    }

    private void processCancellation(Payment payment) {
        payment.updateStatusTo(PaymentStatus.PENDING_REFUND);
        TossPaymentCancelRequest cancelRequest = new TossPaymentCancelRequest("모종의 이유");
        try {
            tossRestClient.cancel(payment.getPaymentKey(), cancelRequest);
            payment.updateStatusTo(PaymentStatus.REFUNDED);
            log.info("결제 취소 성공 - paymentKey={}", payment.getPaymentKey());
        } catch (PaymentTimeoutException e) {
            log.warn("결제 승인 타임아웃 - paymentKey={}", payment.getPaymentKey());
            payment.updateStatusTo(PaymentStatus.REFUND_FAILED);
            // 결제 상태 조회 후 결제 취소 API 호출 (필요 시 구현)
            throw e;
        } catch (TossPaymentException e) {
            log.warn("결제 환불 실패 - paymentKey={}, message={}", payment.getPaymentKey(), e.getMessage());
            payment.updateStatusTo(PaymentStatus.REFUND_FAILED);
            throw e;
        } finally {
            paymentRepository.save(payment);
        }

    }
}
