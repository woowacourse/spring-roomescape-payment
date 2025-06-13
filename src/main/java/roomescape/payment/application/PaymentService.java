package roomescape.payment.application;

import static roomescape.payment.model.PaymentStatus.SUCCESS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.model.Payment;
import roomescape.payment.model.PaymentRepository;
import roomescape.payment.model.TossPaymentApprovalInfo;
import roomescape.payment.model.TossPaymentGateway;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TossPaymentGateway tossPaymentGateway;
    private final PaymentRepository paymentRepository;

    public void requestApprovalToToss(TossPaymentApprovalInfo tossPaymentApprovalInfo) {
        tossPaymentGateway.requestApprove(tossPaymentApprovalInfo);
    }

    @Transactional
    public void updateStatusToSuccess(Long id) {
        Payment payment = paymentRepository.getById(id);
        payment.updateStatusTo(SUCCESS);
        log.debug("결제 성공 후 SUCCESS로 상태 수정");
    }
}
