package roomescape.approval.infrastructure.toss;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.approval.application.command.ApprovalCommandService;
import roomescape.approval.domain.Payment;
import roomescape.approval.domain.repository.ApprovalRepository;
import roomescape.approval.infrastructure.toss.client.TossPaymentClient;
import roomescape.approval.infrastructure.toss.dto.TossPaymentApprovalRequest;

@Service
@AllArgsConstructor
public class TossPaymentCommandCommandService implements ApprovalCommandService<Payment> {

    private final TossPaymentClient tossPaymentClient;
    private final ApprovalRepository approvalRepository;

    @Override
    public boolean supports(Class<?> approvalClass) {
        return Payment.class.equals(approvalClass);
    }

    @Override
    @Transactional
    public void approve(Payment payment) {
        tossPaymentClient.approve(
                new TossPaymentApprovalRequest(payment.getOrderId(), payment.getAmount(), payment.getPaymentKey()));
        payment.approve();
        payment.approveReservation();
        approvalRepository.save(payment);
    }
}
