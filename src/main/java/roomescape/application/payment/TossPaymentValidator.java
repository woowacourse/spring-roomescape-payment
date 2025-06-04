package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.payment.dto.TossPaymentValidationCommand;

@Service
@RequiredArgsConstructor
public class TossPaymentValidator {

    private final OrderAmountVerificationCache orderAmountVerificationCache;

    public void check(final TossPaymentValidationCommand command) {
        orderAmountVerificationCache.check(command.orderId(), command.amount());
    }

    public void register(final TossPaymentValidationCommand command) {
        orderAmountVerificationCache.register(command.orderId(), command.amount());
    }
}
