package roomescape.payment.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.CanceledPayment;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.CanceledPaymentRepository;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.dto.response.ReservationPaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CanceledPaymentRepository canceledPaymentRepository;

    public PaymentService(PaymentRepository paymentRepository, CanceledPaymentRepository canceledPaymentRepository) {
        this.paymentRepository = paymentRepository;
        this.canceledPaymentRepository = canceledPaymentRepository;
    }

    public ReservationPaymentResponse savePayment(PaymentResponse paymentResponse, Reservation reservation) {
        Payment payment = new Payment(paymentResponse.orderId(), paymentResponse.paymentKey(),
                paymentResponse.totalAmount(), reservation, paymentResponse.approvedAt());
        Payment saved = paymentRepository.save(payment);
        return ReservationPaymentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }

    public void cancelPaymentWhenErrorOccurred(PaymentCancelResponse cancelInfo, String paymentKey) {
        cancelPayment(paymentKey, cancelInfo.cancelReason(), cancelInfo.canceledAt());
    }

    public PaymentCancelRequest cancelPaymentByAdmin(Long reservationId) {
        String paymentKey = findPaymentByReservationId(reservationId)
                .orElseThrow(() -> new RoomEscapeException(ErrorType.PAYMENT_NOT_POUND,
                        String.format("[reservationId: %d]", reservationId), HttpStatus.NOT_FOUND))
                .getPaymentKey();
        // 취소 시간은 현재 시간으로 일단 생성한 뒤, 결제 취소 완료 후 해당 시간으로 변경합니다.
        CanceledPayment canceled = cancelPayment(paymentKey, "고객 요청", LocalDateTime.now());

        return new PaymentCancelRequest(paymentKey, canceled.getCancelAmount(), canceled.getCancelReason());
    }

    private CanceledPayment cancelPayment(String paymentKey, String cancelReason, LocalDateTime canceledAt) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> throwPaymentNotFoundByPaymentKey(paymentKey));
        paymentRepository.delete(payment);

        return canceledPaymentRepository.save(new CanceledPayment(paymentKey, cancelReason, payment.getTotalAmount(),
                payment.getApprovedAt(), canceledAt));
    }

    public void updateCanceledTime(String paymentKey, LocalDateTime canceledAt) {
        CanceledPayment canceledPayment = canceledPaymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> throwPaymentNotFoundByPaymentKey(paymentKey));
        canceledPayment.setCanceledAt(canceledAt);
    }

    private RoomEscapeException throwPaymentNotFoundByPaymentKey(String paymentKey) {
        return new RoomEscapeException(
                ErrorType.PAYMENT_NOT_POUND, String.format("[paymentKey: %s]", paymentKey),
                HttpStatus.NOT_FOUND);
    }
}
