package roomescape.application.payment.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.toss.dto.TossPaymentCommand;
import roomescape.domain.payment.repository.TossPaymentRepository;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentValidator tossPaymentValidator;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentStatusUpdater tossPaymentStatusUpdater;

    @Transactional
    public Long save(final TossPaymentCommand command) {
        tossPaymentValidator.check(command.toValidationCommand());
        return tossPaymentRepository.save(command.toDomain()).getId();
    }

    public void approve(final TossPaymentCommand command, final Long tossPaymentId) {
        try {
            tossPaymentClient.approve(command);
        } catch (final Exception e) {
            tossPaymentStatusUpdater.markFailed(tossPaymentId);
            throw e;
        }
        tossPaymentStatusUpdater.markApproved(tossPaymentId);
    }
}
