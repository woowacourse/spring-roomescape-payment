package roomescape.application.payment.toss;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.toss.dto.TossPaymentCommand;
import roomescape.domain.payment.repository.TossPaymentRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class TossPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentValidator tossPaymentValidator;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentStatusUpdater tossPaymentStatusUpdater;

    @Transactional
    public Long save(final TossPaymentCommand command) {
        tossPaymentValidator.check(command.toValidationCommand());
        final Long paymentId = tossPaymentRepository.save(command.toDomain()).getId();
        log.info("결제 정보 저장 완료 - orderId: {}, amount: {}, paymentId: {}", command.orderId(), command.amount(), paymentId);
        return paymentId;
    }

    public void approve(final TossPaymentCommand command, final Long tossPaymentId) {
        try {
            tossPaymentClient.approve(command);
            log.info("결제 승인 완료 - paymentId: {}", tossPaymentId);
        } catch (final Exception e) {
            tossPaymentStatusUpdater.markFailed(tossPaymentId);
            log.error("결제 승인 실패 - paymentId: {}, error: {}", tossPaymentId, e.getMessage());
            throw e;
        }
        tossPaymentStatusUpdater.markApproved(tossPaymentId);
    }
}
