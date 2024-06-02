package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.repository.PaymentRepository;
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
        PaymentRequest paymentRequest = reservationPaymentRequest.toPaymentRequest();
        paymentClient.requestApproval(paymentRequest);

        Payment payment = reservationPaymentRequest.toPayment(PaymentStatus.DONE);
        paymentRepository.save(payment);
    }

    public List<PaymentResponse> findPaidByMemberId(Long memberId) {
        return paymentRepository.findByMemberIdAndStatus(memberId, PaymentStatus.DONE).stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
