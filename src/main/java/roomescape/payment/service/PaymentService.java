package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ErrorType;
import roomescape.exception.NotFoundException;
import roomescape.global.util.Encoder;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.payment.TossPaymentProperties;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.domain.MemberReservation;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    private final Encoder encoder;
    private final TossPaymentProperties tossPaymentProperties;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository, Encoder encoder,
                          TossPaymentProperties tossPaymentProperties) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.encoder = encoder;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    public void pay(PaymentRequest paymentRequest, MemberReservation memberReservation) {
        String encodeKey = encoder.encode(tossPaymentProperties.getSecretKey());
        PaymentResponse response = paymentClient.confirm(paymentRequest, encodeKey).getBody();
        paymentRepository.save(
                Payment.from(response.paymentKey(), response.method(), response.totalAmount(), memberReservation));
    }

    public void refund(long memberReservationId) {
        Payment payment = paymentRepository.findByMemberReservationId(memberReservationId)
                .orElseThrow((() -> new NotFoundException(ErrorType.MEMBER_RESERVATION_NOT_FOUND)));
        String encodeKey = encoder.encode(tossPaymentProperties.getSecretKey());

        paymentClient.cancel(payment.getPaymentKey(), encodeKey);
        paymentRepository.delete(payment);
    }
}
