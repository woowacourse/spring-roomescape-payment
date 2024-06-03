package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.repository.PaymentRepository;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.service.dto.PaymentApproveRequest;
import roomescape.service.dto.PaymentRequest;
import roomescape.service.dto.PaymentResponse;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void requestApproval(PaymentApproveRequest reservationPaymentRequest) {
        validateDuplicate(reservationPaymentRequest.reservationId());
        PaymentRequest paymentRequest = reservationPaymentRequest.toPaymentRequest();
        paymentClient.requestApproval(paymentRequest);

        Payment payment = reservationPaymentRequest.toPayment(PaymentStatus.DONE);
        paymentRepository.save(payment);
    }

    private void validateDuplicate(Long reservationId) {
        paymentRepository.findByReservationIdAndStatus(reservationId, PaymentStatus.DONE)
        .ifPresent(payment -> {
            throw new RoomEscapeBusinessException("이미 결제된 예약입니다.");
        });
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findPaidByMemberId(Long memberId) {
        return paymentRepository.findByMemberIdAndStatus(memberId, PaymentStatus.DONE).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    public void requestRefund(Long reservationId, Long memberId) {
        paymentRepository.findByReservationIdAndMemberIdAndStatus(reservationId, memberId, PaymentStatus.DONE)
                .ifPresent(payment -> {
                    paymentClient.requestRefund(payment.getPaymentKey());
                    payment.refund();
                    paymentRepository.save(payment);
                });
    }
}
