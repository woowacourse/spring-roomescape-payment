package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;
import roomescape.dto.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;
import roomescape.infrastructure.TossPaymentClient;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(TossPaymentClient tossPaymentClient, PaymentRepository paymentRepository) {
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment payment(MemberReservationRequest memberReservationRequest) {
        Long amount = memberReservationRequest.amount();
        String orderId = memberReservationRequest.orderId();
        String paymentKey = memberReservationRequest.paymentKey();

        PaymentInfo paymentInfo = new PaymentInfo(amount, orderId, paymentKey);

        tossPaymentClient.confirmPayment(paymentInfo);

        return savePayment(paymentInfo);
    }

    private Payment savePayment(PaymentInfo paymentInfo) {
        Payment payment = paymentInfo.toEntity();
        return paymentRepository.save(payment);
    }
}
