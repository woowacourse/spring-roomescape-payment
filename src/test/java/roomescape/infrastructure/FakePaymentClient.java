package roomescape.infrastructure;

import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentStatus;
import roomescape.exception.TossPaymentErrorCode;
import roomescape.exception.TossPaymentException;

@Component
@Profile("local")
public class FakePaymentClient implements PaymentClient {

    @Override
    public Payment approve(PaymentRequest request) {
        if (request.paymentKey().equals("failPaymentKey")) {
            throw new TossPaymentException(TossPaymentErrorCode.INTERNAL_SERVER_ERROR);
        }
        return new Payment(
            request.paymentKey(),
            request.amount(),
            LocalDateTime.now().minusSeconds(5).toString(),
            LocalDateTime.now().toString(),
            PaymentStatus.DONE
        );
    }

    @Override
    public void cancel(String paymentKey) {

    }
}
