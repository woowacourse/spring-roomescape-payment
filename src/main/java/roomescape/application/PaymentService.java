package roomescape.application;

import org.springframework.stereotype.Service;
import roomescape.aop.ServiceLogging;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.infrastructure.thirdparty.PaymentRestClient;
import roomescape.infrastructure.thirdparty.dto.PaymentConfirmResponse;
import roomescape.presentation.dto.request.PaymentProcessRequest;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRestClient paymentRestClient,
                          PaymentRepository paymentRepository
    ) {
        this.paymentRestClient = paymentRestClient;
        this.paymentRepository = paymentRepository;
    }

    @ServiceLogging
    public Payment processPayment(PaymentProcessRequest request, Reservation reservation) {
        PaymentConfirmResponse response = paymentRestClient.getPaymentResponse(request);
        String paymentKey = response.paymentKey();
        String orderId = request.orderId();
        BigDecimal amount = new BigDecimal(request.amount());
        Payment payment = Payment.create(paymentKey, orderId, amount, reservation);
        return paymentRepository.save(payment);
    }

    public List<Payment> findPaymentsByMember(Member member) {
        return paymentRepository.findAllByMemberId(member.getId());
    }
}
