package roomescape.application.payment.toss;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.application.payment.OrderAmountVerificationCache;
import roomescape.application.payment.toss.dto.TossPaymentValidationCommand;

@Slf4j
@RequiredArgsConstructor
@Service
public class TossPaymentValidator {

    private final OrderAmountVerificationCache orderAmountVerificationCache;

    public void check(final TossPaymentValidationCommand command) {
        orderAmountVerificationCache.check(command.orderId(), command.amount());
        log.info("결제 검증 완료 - orderId: {}, amount: {}", command.orderId(), command.amount());
    }

    public void register(final TossPaymentValidationCommand command) {
        log.info("결제 정보 등록 - orderId: {}, amount: {}", command.orderId(), command.amount());
        orderAmountVerificationCache.register(command.orderId(), command.amount());
    }
}
