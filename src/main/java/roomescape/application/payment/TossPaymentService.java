package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.payment.dto.TossPaymentCommand;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentValidator tossPaymentValidator;

    public void approve(final TossPaymentCommand command) {
        tossPaymentValidator.check(command.toValidationCommand());
        tossPaymentClient.approve(command);
    }
}
