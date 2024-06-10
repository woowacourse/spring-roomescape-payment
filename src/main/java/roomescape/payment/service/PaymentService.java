package roomescape.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ErrorType;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
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

    public PaymentService(PaymentClient paymentClient,
                          PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void pay(PaymentRequest paymentRequest, MemberReservation memberReservation) {
        log.info("[결제 요청] payment: {}, member email: {}", paymentRequest, memberReservation.getMember().getEmail());
        PaymentResponse response = paymentClient.confirm(paymentRequest);
        paymentRepository.save(
                Payment.from(response.paymentKey(), response.method(), response.totalAmount(), memberReservation));
    }


    @Transactional
    public void refund(long memberReservationId) {
        Payment payment = paymentRepository.findByMemberReservationId(memberReservationId)
                .orElseThrow((() -> new NotFoundException(ErrorType.MEMBER_RESERVATION_NOT_FOUND)));

        log.info("[환불 요청] payment: {}", payment);

        paymentRepository.delete(payment);
        paymentClient.cancel(payment.getPaymentKey());
    }
}
