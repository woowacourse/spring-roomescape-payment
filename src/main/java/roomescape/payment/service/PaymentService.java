package roomescape.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ErrorType;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentHistory;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.domain.repository.PaymentHistoryRepository;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.reservation.domain.MemberReservation;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PaymentClient paymentClient;

    private final PaymentRepository paymentRepository;

    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentService(PaymentClient paymentClient,
                          PaymentRepository paymentRepository,
                          PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    @Transactional
    public Payment pay(PaymentRequest paymentRequest, MemberReservation memberReservation) {
        log.info("[결제 요청] payment: {}, member email: {}", paymentRequest, memberReservation.getMember().getEmail());
        PaymentResponse response = paymentClient.confirm(paymentRequest);
        return paymentRepository.save(
                Payment.from(response.paymentKey(), response.method(), response.totalAmount(), memberReservation));
    }

    public void createHistory(MemberReservation memberReservation, Payment payment) {
        paymentHistoryRepository.save(
                new PaymentHistory(
                        payment.getPaymentKey(),
                        payment.getPaymentType(),
                        PaymentStatus.PAID,
                        payment.getAmount(),
                        memberReservation.getMember()
                )
        );
    }

    @Transactional
    public void refund(long memberReservationId) {
        Payment payment = paymentRepository.findByMemberReservationId(memberReservationId)
                .orElseThrow((() -> new NotFoundException(ErrorType.MEMBER_RESERVATION_NOT_FOUND)));

        log.info("[환불 요청] payment: {}", payment);

        paymentRepository.delete(payment);
        updateHistory(payment.getPaymentKey());
        paymentClient.cancel(payment.getPaymentKey());
    }

    private void updateHistory(String paymentKey) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findPaymentHistoryByPaymentKey(paymentKey)
                .orElseThrow(IllegalStateException::new);
        paymentHistory.cancel();
    }
}
