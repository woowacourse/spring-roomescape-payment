package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.payment.dto.PaymentCommand;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentValidator paymentValidator;

    public void approve(final PaymentCommand command) {
        paymentValidator.check(command.toValidationCommand());
        tossPaymentClient.approve(command);
    }
}
