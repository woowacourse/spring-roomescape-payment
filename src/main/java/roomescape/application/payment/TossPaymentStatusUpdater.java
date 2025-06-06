package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.TossPayment;
import roomescape.domain.payment.repository.TossPaymentRepository;
import roomescape.infrastructure.error.exception.PaymentException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossPaymentStatusUpdater {

    private final TossPaymentRepository tossPaymentRepository;

    @Transactional
    public void markApproved(final Long tossPaymentId) {
        getTossPayment(tossPaymentId).approve();
    }

    @Transactional
    public void markFailed(final Long tossPaymentId) {
        getTossPayment(tossPaymentId).fail();
    }

    private TossPayment getTossPayment(final Long tossPaymentId) {
        return tossPaymentRepository.findById(tossPaymentId)
                .orElseThrow(() -> {
                    log.error("결제 ID={} 결제 정보 업데이트 시점에 결제 엔티티가 존재하지 않음", tossPaymentId);
                    return new PaymentException("존재하지 않는 결제입니다");
                });
    }
}
