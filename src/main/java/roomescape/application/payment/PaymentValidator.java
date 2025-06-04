package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.payment.dto.PaymentValidationCommand;

@Service
@RequiredArgsConstructor
public class PaymentValidator {

    private final OrderAmountVerificationCache orderAmountVerificationCache;

    public void check(final PaymentValidationCommand command) {
        orderAmountVerificationCache.check(command.orderId(), command.amount());
    }

    public void register(final PaymentValidationCommand command) {
        orderAmountVerificationCache.register(command.orderId(), command.amount());
    }
}
