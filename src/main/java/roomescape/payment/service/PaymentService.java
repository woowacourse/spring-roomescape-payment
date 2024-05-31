package roomescape.payment.service;

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
    public void pay(PaymentRequest paymentRequest, MemberReservation memberReservation) {
        PaymentResponse response = paymentClient.confirm(paymentRequest);
        Payment payment = paymentRepository.save(
                Payment.from(response.paymentKey(), response.method(), response.totalAmount(), memberReservation));

        createHistory(memberReservation, payment);
    }

    private void createHistory(MemberReservation memberReservation, Payment payment) {
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
